import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { LegacyReleaseReadiness } from './LegacyReleaseReadiness.js';

export class LegacyReleaseGate {
  canRelease(readiness: LegacyReleaseReadiness): boolean {
    requireNonNull(readiness, 'readiness');
    return !readiness.notApproved
      && !readiness.testsNotPassed
      && !readiness.windowNotOpen;
  }
}
