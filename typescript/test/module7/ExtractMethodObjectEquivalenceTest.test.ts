import { describe, expect, it } from 'vitest';
import { IllegalArgumentError, NullPointerError } from '../../src/shared/errors.js';
import { DeploymentRiskInput } from '../../src/module7/methodobject/DeploymentRiskInput.js';
import { RiskAssessment } from '../../src/module7/methodobject/RiskAssessment.js';
import { RiskLevel } from '../../src/module7/methodobject/RiskLevel.js';
import { DeploymentRiskCalculator } from '../../src/module7/methodobject/after/DeploymentRiskCalculator.js';
import { LegacyDeploymentRiskCalculator } from '../../src/module7/methodobject/before/LegacyDeploymentRiskCalculator.js';

const NULL = null as never;
const INTEGER_MAX_VALUE = 2_147_483_647;

function failureOf(action: () => unknown): Error {
  try {
    action();
  } catch (error) {
    return error as Error;
  }
  throw new Error('Expected action to throw');
}

function assertInvalidInput(constructor: () => unknown, expectedMessage: string): void {
  const failure = failureOf(constructor);
  expect(failure).toBeInstanceOf(IllegalArgumentError);
  expect(failure.message).toBe(expectedMessage);
}

describe('ExtractMethodObjectEquivalenceTest', () => {
  it('preservesRiskAssessmentsAcrossRepresentativeInputs', () => {
    const before = new LegacyDeploymentRiskCalculator();
    const after = new DeploymentRiskCalculator();
    const inputs = [
      new DeploymentRiskInput(0, 0, 0, false),
      new DeploymentRiskInput(5, 0, 0, true),
      new DeploymentRiskInput(10, 2, 1, true),
      new DeploymentRiskInput(29, 0, 0, false),
      new DeploymentRiskInput(30, 0, 0, false),
      new DeploymentRiskInput(69, 0, 0, false),
      new DeploymentRiskInput(70, 0, 0, false),
      new DeploymentRiskInput(INTEGER_MAX_VALUE,
        INTEGER_MAX_VALUE,
        INTEGER_MAX_VALUE,
        false)];

    for (const input of inputs) {
      expect(after.calculate(input), 'Different assessment for '
        + JSON.stringify(input)).toEqual(before.calculate(input));
    }
  });

  it('preservesScoreClampingAndClassificationBoundaries', () => {
    const calculator = new DeploymentRiskCalculator();

    expect(calculator.calculate(new DeploymentRiskInput(5, 0, 0, true)))
      .toEqual(new RiskAssessment(0, RiskLevel.LOW));
    expect(calculator.calculate(new DeploymentRiskInput(29, 0, 0, false)))
      .toEqual(new RiskAssessment(29, RiskLevel.LOW));
    expect(calculator.calculate(new DeploymentRiskInput(30, 0, 0, false)))
      .toEqual(new RiskAssessment(30, RiskLevel.MEDIUM));
    expect(calculator.calculate(new DeploymentRiskInput(69, 0, 0, false)))
      .toEqual(new RiskAssessment(69, RiskLevel.MEDIUM));
    expect(calculator.calculate(new DeploymentRiskInput(70, 0, 0, false)))
      .toEqual(new RiskAssessment(70, RiskLevel.HIGH));
    expect(calculator.calculate(new DeploymentRiskInput(
      INTEGER_MAX_VALUE,
      INTEGER_MAX_VALUE,
      INTEGER_MAX_VALUE,
      false)))
      .toEqual(new RiskAssessment(100, RiskLevel.HIGH));
  });

  it('preservesEachRiskWeightAndRollbackReduction', () => {
    const calculator = new DeploymentRiskCalculator();

    expect(calculator.calculate(new DeploymentRiskInput(0, 1, 0, false)))
      .toEqual(new RiskAssessment(20, RiskLevel.LOW));
    expect(calculator.calculate(new DeploymentRiskInput(0, 0, 1, false)))
      .toEqual(new RiskAssessment(10, RiskLevel.LOW));
    expect(calculator.calculate(new DeploymentRiskInput(50, 0, 0, true)))
      .toEqual(new RiskAssessment(35, RiskLevel.MEDIUM));
  });

  it('facadeCreatesAnIndependentCalculationForEveryInvocation', () => {
    const calculator = new DeploymentRiskCalculator();
    const low = new DeploymentRiskInput(10, 0, 0, false);
    const high = new DeploymentRiskInput(80, 0, 0, false);

    expect(calculator.calculate(low)).toEqual(new RiskAssessment(10, RiskLevel.LOW));
    expect(calculator.calculate(high)).toEqual(new RiskAssessment(80, RiskLevel.HIGH));
    expect(calculator.calculate(low)).toEqual(new RiskAssessment(10, RiskLevel.LOW));
  });

  it('preservesMissingInputFailure', () => {
    const before = new LegacyDeploymentRiskCalculator();
    const after = new DeploymentRiskCalculator();

    const beforeFailure = failureOf(() => before.calculate(NULL));
    const afterFailure = failureOf(() => after.calculate(NULL));

    expect(beforeFailure).toBeInstanceOf(NullPointerError);
    expect(afterFailure).toBeInstanceOf(NullPointerError);
    expect(afterFailure.message).toBe(beforeFailure.message);
  });

  it('sharedInputModelRejectsNegativeCounts', () => {
    assertInvalidInput(
      () => new DeploymentRiskInput(-1, 0, 0, false),
      'changedFiles must not be negative');
    assertInvalidInput(
      () => new DeploymentRiskInput(0, -1, 0, false),
      'criticalServices must not be negative');
    assertInvalidInput(
      () => new DeploymentRiskInput(0, 0, -1, false),
      'failedChecks must not be negative');
  });

  it('assessmentProtectsItsRangeAndRequiredLevel', () => {
    const belowRange = failureOf(() => new RiskAssessment(-1, RiskLevel.LOW));
    const aboveRange = failureOf(() => new RiskAssessment(101, RiskLevel.HIGH));
    const missingLevel = failureOf(() => new RiskAssessment(10, NULL));

    expect(belowRange).toBeInstanceOf(IllegalArgumentError);
    expect(aboveRange).toBeInstanceOf(IllegalArgumentError);
    expect(missingLevel).toBeInstanceOf(NullPointerError);
    expect(belowRange.message).toBe('score must be between 0 and 100');
    expect(aboveRange.message).toBe('score must be between 0 and 100');
    expect(missingLevel.message).toBe('level');
  });
});
