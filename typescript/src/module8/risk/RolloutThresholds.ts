import { IllegalArgumentError } from '../../shared/errors.js';

export class RolloutThresholds {
  readonly minimumSampleSize: number;
  readonly maximumErrorRate: number;
  readonly maximumP95LatencyMillis: number;

  constructor(
    minimumSampleSize: number,
    maximumErrorRate: number,
    maximumP95LatencyMillis: number,
  ) {
    // Java: long — w TS number, więc całkowitość sprawdzamy jawnie.
    if (!Number.isSafeInteger(minimumSampleSize)) {
      throw new IllegalArgumentError('minimumSampleSize must be an integer');
    }
    if (minimumSampleSize < 1) {
      throw new IllegalArgumentError('minimumSampleSize must be positive');
    }
    RolloutThresholds.requireFiniteInRange(maximumErrorRate, 0.0, 1.0, 'maximumErrorRate');
    RolloutThresholds.requireFiniteInRange(
      maximumP95LatencyMillis,
      0.0,
      Number.MAX_VALUE,
      'maximumP95LatencyMillis',
    );
    this.minimumSampleSize = minimumSampleSize;
    this.maximumErrorRate = maximumErrorRate;
    this.maximumP95LatencyMillis = maximumP95LatencyMillis;
  }

  private static requireFiniteInRange(
    value: number,
    minimum: number,
    maximum: number,
    name: string,
  ): void {
    if (!Number.isFinite(value) || value < minimum || value > maximum) {
      throw new IllegalArgumentError(
        `${name} must be finite and between ${javaDouble(minimum)} and ${javaDouble(maximum)}`,
      );
    }
  }
}

// Formatuje liczbę jak Double.toString w Javie (0.0, 1.0, 1.7976931348623157E308).
function javaDouble(value: number): string {
  if (Number.isInteger(value) && Math.abs(value) < 1e7) {
    return value.toFixed(1);
  }
  if (Math.abs(value) >= 1e7 || (value !== 0 && Math.abs(value) < 1e-3)) {
    const [mantissa = '', exponent = ''] = value.toExponential().split('e');
    const javaMantissa = mantissa.includes('.') ? mantissa : `${mantissa}.0`;
    return `${javaMantissa}E${exponent.replace('+', '')}`;
  }
  return String(value);
}
