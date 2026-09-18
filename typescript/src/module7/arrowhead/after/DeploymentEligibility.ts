import type { DeploymentCandidate } from '../DeploymentCandidate.js';
import { Eligibility } from '../Eligibility.js';

export class DeploymentEligibility {
  evaluate(candidate: DeploymentCandidate | null): Eligibility {
    if (candidate == null) {
      return Eligibility.MISSING_CANDIDATE;
    }
    if (candidate.releaseId == null
        || candidate.releaseId.trim() === '') {
      return Eligibility.INVALID_RELEASE_ID;
    }
    if (!candidate.approved) {
      return Eligibility.NOT_APPROVED;
    }
    if (!candidate.testsPassed) {
      return Eligibility.TESTS_FAILED;
    }
    if (!candidate.windowOpen) {
      return Eligibility.WINDOW_CLOSED;
    }
    return Eligibility.ELIGIBLE;
  }
}
