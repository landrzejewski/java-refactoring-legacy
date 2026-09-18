import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../../src/shared/errors.js';
import { RolloutSnapshot } from '../../../src/module8/risk/RolloutSnapshot.js';

describe('RolloutSnapshotTest', () => {
  it('calculatesErrorRateAndDefinesItForAnEmptySample', () => {
    expect(new RolloutSnapshot(20, 5, 0, 100.0).errorRate()).toBe(0.25);
    expect(new RolloutSnapshot(0, 0, 0, 0.0).errorRate()).toBe(0.0);
  });

  it('acceptsCountsAtBothBoundaries', () => {
    expect(() => new RolloutSnapshot(1, 0, 0, 0.0)).not.toThrow();
    expect(() => new RolloutSnapshot(1, 1, 1, Number.MAX_VALUE)).not.toThrow();
  });

  it('rejectsNegativeOrInconsistentCounts', () => {
    expect(() => new RolloutSnapshot(-1, 0, 0, 0.0)).toThrow(IllegalArgumentError);
    expect(() => new RolloutSnapshot(10, -1, 0, 0.0)).toThrow(IllegalArgumentError);
    expect(() => new RolloutSnapshot(10, 11, 0, 0.0)).toThrow(IllegalArgumentError);
    expect(() => new RolloutSnapshot(10, 0, -1, 0.0)).toThrow(IllegalArgumentError);
    expect(() => new RolloutSnapshot(10, 0, 11, 0.0)).toThrow(IllegalArgumentError);
  });

  it('rejectsInvalidLatencyMeasurements', () => {
    const invalidValues = [
      Number.NEGATIVE_INFINITY,
      -Number.MIN_VALUE,
      -1.0,
      Number.POSITIVE_INFINITY,
      Number.NaN,
    ];

    for (const invalid of invalidValues) {
      expect(() => new RolloutSnapshot(10, 0, 0, invalid)).toThrow(IllegalArgumentError);
    }
  });

  it('requiresZeroLatencyForAnEmptySample', () => {
    expect(() => new RolloutSnapshot(0, 0, 0, Number.MIN_VALUE)).toThrow(IllegalArgumentError);
  });
});
