import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import { isBlank } from '../../support.js';

// Odpowiednik zagnieżdżonego enuma BuildReport.Status.
export enum Status {
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED',
  RUNNING = 'RUNNING',
}

const COMMIT = /^[0-9a-f]{7,40}$/;

/**
 * Obiekt graniczny (Boundary Object): niezmienny, zwalidowany obraz wyniku buildu.
 * Jeśli istnieje, jest poprawny - domena nie musi już niczego parsować ani sprawdzać.
 */
export class BuildReport {
  constructor(
    readonly buildId: string,
    readonly status: Status,
    readonly coverage: number,
    readonly failedTests: number,
    readonly commit: string,
  ) {
    if (isBlank(buildId)) {
      throw new IllegalArgumentError('buildId must not be blank');
    }
    requireNonNull(status, 'status');
    if (coverage < 0 || coverage > 100) {
      throw new IllegalArgumentError('coverage must be between 0 and 100');
    }
    if (failedTests < 0) {
      throw new IllegalArgumentError('failedTests must not be negative');
    }
    if (commit == null || !COMMIT.test(commit)) {
      throw new IllegalArgumentError('commit must be a hex sha');
    }
  }

  succeeded(): boolean {
    return this.status === Status.SUCCESS;
  }
}
