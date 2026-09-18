import { describe, expect, it } from 'vitest';
import { ArithmeticError, IllegalArgumentError, NullPointerError } from '../../src/shared/errors.js';
import { DeploymentMetrics } from '../../src/module7/breakresponsibilities/DeploymentMetrics.js';
import { DeploymentSample } from '../../src/module7/breakresponsibilities/DeploymentSample.js';
import { DeploymentMetricsCalculator } from '../../src/module7/breakresponsibilities/after/DeploymentMetricsCalculator.js';
import { DeploymentReportFormatter } from '../../src/module7/breakresponsibilities/after/DeploymentReportFormatter.js';
import { DeploymentReportService } from '../../src/module7/breakresponsibilities/after/DeploymentReportService.js';
import { LegacyDeploymentReport } from '../../src/module7/breakresponsibilities/before/LegacyDeploymentReport.js';

const NULL = null as never;

function failureOf(action: () => unknown): Error {
  try {
    action();
  } catch (error) {
    return error as Error;
  }
  throw new Error('Expected action to throw');
}

function assertSameFailure(
  beforeAction: () => unknown,
  afterAction: () => unknown,
): void {
  const before = failureOf(beforeAction);
  const after = failureOf(afterAction);

  expect(after.constructor).toBe(before.constructor);
  expect(after.message).toBe(before.message);
}

function service(): DeploymentReportService {
  return new DeploymentReportService(
    new DeploymentMetricsCalculator(),
    new DeploymentReportFormatter());
}

describe('BreakResponsibilitiesEquivalenceTest', () => {
  it('preservesGeneratedReport', () => {
    const samples = [
      new DeploymentSample(12, true),
      new DeploymentSample(18, false),
      new DeploymentSample(24, true)];
    const before = new LegacyDeploymentReport();
    const after = service();

    expect(before.generate(samples))
      .toBe('deployments=3;failures=1;avgLeadTimeMinutes=18');
    expect(after.generate(samples)).toBe(before.generate(samples));
  });

  it('preservesEmptyReportAndIntegerAverage', () => {
    const before = new LegacyDeploymentReport();
    const after = service();

    expect(before.generate([]))
      .toBe('deployments=0;failures=0;avgLeadTimeMinutes=0');
    expect(after.generate([])).toBe(before.generate([]));

    const samples = [
      new DeploymentSample(1, true),
      new DeploymentSample(2, true)];
    expect(before.generate(samples))
      .toBe('deployments=2;failures=0;avgLeadTimeMinutes=1');
    expect(after.generate(samples)).toBe(before.generate(samples));
  });

  it('neitherImplementationMutatesTheInputList', () => {
    const samples = [
      new DeploymentSample(30, false),
      new DeploymentSample(10, true)];
    const snapshot = [...samples];

    new LegacyDeploymentReport().generate(samples);
    expect(samples).toEqual(snapshot);

    service().generate(samples);
    expect(samples).toEqual(snapshot);
  });

  it('preservesFailuresForMissingListAndElement', () => {
    const before = new LegacyDeploymentReport();
    const after = service();

    assertSameFailure(
      () => before.generate(NULL),
      () => after.generate(NULL));

    const containingNull: DeploymentSample[] = [
      new DeploymentSample(10, true), NULL];
    assertSameFailure(
      () => before.generate(containingNull),
      () => after.generate(containingNull));
  });

  it('preservesOverflowFailureWhileAccumulatingLeadTime', () => {
    // Long.MAX_VALUE → Number.MAX_SAFE_INTEGER: górna granica dokładnych
    // liczb całkowitych w JS, powyżej której addExact zgłasza przepełnienie.
    const samples = [
      new DeploymentSample(Number.MAX_SAFE_INTEGER, true),
      new DeploymentSample(1, false)];
    const before = new LegacyDeploymentReport();
    const after = service();

    assertSameFailure(
      () => before.generate(samples),
      () => after.generate(samples));
    expect(() => after.generate(samples)).toThrow(ArithmeticError);
  });

  it('extractedComponentsHaveFocusedContracts', () => {
    const calculator = new DeploymentMetricsCalculator();
    const formatter = new DeploymentReportFormatter();
    const samples = [
      new DeploymentSample(15, true),
      new DeploymentSample(25, false)];

    const metrics = calculator.calculate(samples);

    expect(metrics).toEqual(new DeploymentMetrics(2, 1, 20));
    expect(formatter.format(metrics))
      .toBe('deployments=2;failures=1;avgLeadTimeMinutes=20');
  });

  it('domainValuesAndCollaboratorsRejectInvalidState', () => {
    const invalidSample = failureOf(() => new DeploymentSample(-1, true));
    const invalidFailureCount = failureOf(() => new DeploymentMetrics(1, 2, 10));
    const invalidEmptyAverage = failureOf(() => new DeploymentMetrics(0, 0, 1));
    const missingMetrics = failureOf(
      () => new DeploymentReportFormatter().format(NULL));
    const missingCalculator = failureOf(
      () => new DeploymentReportService(NULL, new DeploymentReportFormatter()));
    const missingFormatter = failureOf(
      () => new DeploymentReportService(new DeploymentMetricsCalculator(), NULL));

    expect(invalidSample).toBeInstanceOf(IllegalArgumentError);
    expect(invalidFailureCount).toBeInstanceOf(IllegalArgumentError);
    expect(invalidEmptyAverage).toBeInstanceOf(IllegalArgumentError);
    expect(missingMetrics).toBeInstanceOf(NullPointerError);
    expect(missingCalculator).toBeInstanceOf(NullPointerError);
    expect(missingFormatter).toBeInstanceOf(NullPointerError);

    expect(invalidSample.message).toBe('leadTimeMinutes must not be negative');
    expect(invalidFailureCount.message)
      .toBe('failures must be between 0 and deployments');
    expect(invalidEmptyAverage.message)
      .toBe('empty metrics must have zero average lead time');
    expect(missingMetrics.message).toBe('metrics');
    expect(missingCalculator.message).toBe('calculator');
    expect(missingFormatter.message).toBe('formatter');
  });
});
