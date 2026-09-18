import { describe, expect, it } from 'vitest';
import { NullPointerError } from '../../../src/shared/errors.js';
import { RolloutDecision } from '../../../src/module8/risk/RolloutDecision.js';
import { RolloutPolicy } from '../../../src/module8/risk/RolloutPolicy.js';
import { RolloutSnapshot } from '../../../src/module8/risk/RolloutSnapshot.js';
import { RolloutThresholds } from '../../../src/module8/risk/RolloutThresholds.js';

describe('RolloutPolicyTest', () => {
  const policy = new RolloutPolicy(new RolloutThresholds(100, 0.05, 250.0));

  it('advancesWhenTheSampleAndMetricsMeetAllThresholds', () => {
    expect(policy.decide(new RolloutSnapshot(200, 4, 0, 180.0))).toBe(RolloutDecision.ADVANCE);
  });

  it('advancesAtInclusiveMetricAndSampleBoundaries', () => {
    expect(policy.decide(new RolloutSnapshot(100, 5, 0, 250.0))).toBe(RolloutDecision.ADVANCE);
  });

  it('holdsWhenAHealthySampleIsStillTooSmall', () => {
    expect(policy.decide(new RolloutSnapshot(99, 0, 0, 120.0))).toBe(RolloutDecision.HOLD);
    expect(policy.decide(new RolloutSnapshot(0, 0, 0, 0.0))).toBe(RolloutDecision.HOLD);
  });

  it('rollsBackAfterAnyBehaviorMismatch', () => {
    expect(policy.decide(new RolloutSnapshot(200, 0, 1, 120.0))).toBe(RolloutDecision.ROLLBACK);
  });

  it('rollsBackAfterAnAbsoluteErrorOrLatencySloViolation', () => {
    expect(policy.decide(new RolloutSnapshot(100, 6, 0, 200.0))).toBe(RolloutDecision.ROLLBACK);
    expect(policy.decide(new RolloutSnapshot(100, 0, 0, 250.01))).toBe(RolloutDecision.ROLLBACK);
  });

  it('safetyViolationsTakePriorityOverAnInsufficientSample', () => {
    expect(policy.decide(new RolloutSnapshot(10, 0, 1, 100.0))).toBe(RolloutDecision.ROLLBACK);
    expect(policy.decide(new RolloutSnapshot(10, 1, 0, 100.0))).toBe(RolloutDecision.ROLLBACK);
    expect(policy.decide(new RolloutSnapshot(10, 0, 0, 251.0))).toBe(RolloutDecision.ROLLBACK);
  });

  it('rejectsMissingPolicyInputs', () => {
    expect(() => new RolloutPolicy(null as unknown as RolloutThresholds)).toThrow(NullPointerError);
    expect(() => policy.decide(null as unknown as RolloutSnapshot)).toThrow(NullPointerError);
  });
});
