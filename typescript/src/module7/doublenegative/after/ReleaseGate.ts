import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { ReleaseReadiness } from './ReleaseReadiness.js';

export class ReleaseGate {
  canRelease(readiness: ReleaseReadiness): boolean {
    requireNonNull(readiness, 'readiness');
    return readiness.approved
      && readiness.testsPassed
      && readiness.windowOpen;
  }
}
