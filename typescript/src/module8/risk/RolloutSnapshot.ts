import { IllegalArgumentError } from '../../shared/errors.js';

export class RolloutSnapshot {
  readonly sampleSize: number;
  readonly failedRequests: number;
  readonly mismatchedResponses: number;
  readonly p95LatencyMillis: number;

  constructor(
    sampleSize: number,
    failedRequests: number,
    mismatchedResponses: number,
    p95LatencyMillis: number,
  ) {
    // Java: long — w TS number, więc całkowitość sprawdzamy jawnie.
    if (!Number.isSafeInteger(sampleSize)) {
      throw new IllegalArgumentError('sampleSize must be an integer');
    }
    if (sampleSize < 0) {
      throw new IllegalArgumentError('sampleSize must not be negative');
    }
    RolloutSnapshot.requireCountWithinSample(failedRequests, sampleSize, 'failedRequests');
    RolloutSnapshot.requireCountWithinSample(
      mismatchedResponses,
      sampleSize,
      'mismatchedResponses',
    );
    if (!Number.isFinite(p95LatencyMillis) || p95LatencyMillis < 0.0) {
      throw new IllegalArgumentError('p95LatencyMillis must be finite and not negative');
    }
    if (sampleSize === 0 && p95LatencyMillis !== 0.0) {
      throw new IllegalArgumentError('an empty sample must have zero p95LatencyMillis');
    }
    this.sampleSize = sampleSize;
    this.failedRequests = failedRequests;
    this.mismatchedResponses = mismatchedResponses;
    this.p95LatencyMillis = p95LatencyMillis;
  }

  errorRate(): number {
    return this.sampleSize === 0 ? 0.0 : this.failedRequests / this.sampleSize;
  }

  private static requireCountWithinSample(count: number, sampleSize: number, name: string): void {
    if (!Number.isSafeInteger(count)) {
      throw new IllegalArgumentError(`${name} must be an integer`);
    }
    if (count < 0 || count > sampleSize) {
      throw new IllegalArgumentError(`${name} must be between 0 and sampleSize`);
    }
  }
}
