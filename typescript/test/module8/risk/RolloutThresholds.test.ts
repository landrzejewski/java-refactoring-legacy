import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../../src/shared/errors.js';
import { RolloutThresholds } from '../../../src/module8/risk/RolloutThresholds.js';

describe('RolloutThresholdsTest', () => {
  it('acceptsInclusiveNumericBoundaries', () => {
    expect(new RolloutThresholds(1, 0.0, 0.0)).toEqual(new RolloutThresholds(1, 0.0, 0.0));
    // Long.MAX_VALUE → największa bezpieczna liczba całkowita w JS.
    expect(
      new RolloutThresholds(Number.MAX_SAFE_INTEGER, 1.0, Number.MAX_VALUE).maximumErrorRate,
    ).toBe(1.0);
  });

  it('rejectsNonPositiveMinimumSampleSize', () => {
    for (const invalid of [Number.MIN_SAFE_INTEGER, -1, 0]) {
      expect(() => new RolloutThresholds(invalid, 0.05, 250.0)).toThrow(IllegalArgumentError);
    }
  });

  it('rejectsInvalidErrorRates', () => {
    const invalidValues = [
      Number.NEGATIVE_INFINITY,
      -Number.MIN_VALUE,
      -1.0,
      1.0 + Number.EPSILON, // Math.nextUp(1.0)
      Number.POSITIVE_INFINITY,
      Number.NaN,
    ];

    for (const invalid of invalidValues) {
      expect(() => new RolloutThresholds(100, invalid, 250.0)).toThrow(IllegalArgumentError);
    }
  });

  it('rejectsInvalidLatencyThresholds', () => {
    const invalidValues = [
      Number.NEGATIVE_INFINITY,
      -Number.MIN_VALUE,
      -1.0,
      Number.POSITIVE_INFINITY,
      Number.NaN,
    ];

    for (const invalid of invalidValues) {
      expect(() => new RolloutThresholds(100, 0.05, invalid)).toThrow(IllegalArgumentError);
    }
  });
});
