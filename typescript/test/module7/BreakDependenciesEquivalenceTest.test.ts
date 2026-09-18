import { describe, expect, it } from 'vitest';
import { IllegalArgumentError, NullPointerError } from '../../src/shared/errors.js';
import { DeploymentDecision } from '../../src/module7/breakdependencies/DeploymentDecision.js';
import { DeploymentWindowService } from '../../src/module7/breakdependencies/after/DeploymentWindowService.js';
import { StandardMaintenanceWindows } from '../../src/module7/breakdependencies/after/StandardMaintenanceWindows.js';
import { LegacyDeploymentWindowService } from '../../src/module7/breakdependencies/before/LegacyDeploymentWindowService.js';

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

describe('BreakDependenciesEquivalenceTest', () => {
  it('standardDependencyPreservesDecisionsForEveryHour', () => {
    const before = new LegacyDeploymentWindowService();
    const after = new DeploymentWindowService(
      new StandardMaintenanceWindows());

    for (let hourUtc = 0; hourUtc < 24; hourUtc++) {
      expect(
        after.schedule('payments', hourUtc),
        'Different decision for hour ' + hourUtc,
      ).toBe(before.schedule('payments', hourUtc));
    }
  });

  it('preservesMaintenanceWindowBoundaries', () => {
    const service = new DeploymentWindowService(
      new StandardMaintenanceWindows());

    expect(service.schedule('payments', 0)).toBe(DeploymentDecision.ALLOWED);
    expect(service.schedule('payments', 5)).toBe(DeploymentDecision.ALLOWED);
    expect(service.schedule('payments', 6))
      .toBe(DeploymentDecision.OUTSIDE_MAINTENANCE_WINDOW);
    expect(service.schedule('payments', 23))
      .toBe(DeploymentDecision.OUTSIDE_MAINTENANCE_WINDOW);
  });

  it('injectedSeamControlsTheDecisionAndReceivesTheArgumentsOnce', () => {
    let calls = 0;
    let receivedService: string | undefined;
    let receivedHour = -1;
    const service = new DeploymentWindowService({
      allows(candidate, hourUtc) {
        calls++;
        receivedService = candidate;
        receivedHour = hourUtc;
        return candidate === 'emergency' && hourUtc === 14;
      },
    });

    expect(service.schedule('emergency', 14)).toBe(DeploymentDecision.ALLOWED);
    expect(calls).toBe(1);
    expect(receivedService).toBe('emergency');
    expect(receivedHour).toBe(14);
  });

  it('preservesValidationFailuresAndTheirOrder', () => {
    const before = new LegacyDeploymentWindowService();
    const after = new DeploymentWindowService(
      new StandardMaintenanceWindows());

    assertSameFailure(
      () => before.schedule(NULL, 2),
      () => after.schedule(NULL, 2));
    assertSameFailure(
      () => before.schedule('   ', 2),
      () => after.schedule('   ', 2));
    assertSameFailure(
      () => before.schedule('payments', -1),
      () => after.schedule('payments', -1));
    assertSameFailure(
      () => before.schedule('payments', 24),
      () => after.schedule('payments', 24));
    assertSameFailure(
      () => before.schedule(' ', -1),
      () => after.schedule(' ', -1));
  });

  it('invalidRequestDoesNotReachInjectedDependency', () => {
    let calls = 0;
    const service = new DeploymentWindowService({
      allows() {
        calls++;
        return true;
      },
    });

    expect(() => service.schedule(NULL, 2)).toThrow(NullPointerError);
    expect(() => service.schedule(' ', 2)).toThrow(IllegalArgumentError);
    expect(() => service.schedule('payments', -1)).toThrow(IllegalArgumentError);
    expect(() => service.schedule('payments', 24)).toThrow(IllegalArgumentError);
    expect(() => service.schedule(' ', -1)).toThrow(IllegalArgumentError);
    expect(calls).toBe(0);
  });

  it('rejectsMissingInjectedDependency', () => {
    const failure = failureOf(() => new DeploymentWindowService(NULL));

    expect(failure).toBeInstanceOf(NullPointerError);
    expect(failure.message).toBe('maintenanceWindows');
  });
});
