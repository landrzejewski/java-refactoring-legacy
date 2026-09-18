import type { DeploymentCandidate } from '../DeploymentCandidate.js';
import { Eligibility } from '../Eligibility.js';

export class LegacyDeploymentEligibility {
  evaluate(candidate: DeploymentCandidate | null): Eligibility {
    let result: Eligibility;
    if (candidate != null) {
      if (candidate.releaseId != null
          && candidate.releaseId.trim() !== '') {
        if (candidate.approved) {
          if (candidate.testsPassed) {
            if (candidate.windowOpen) {
              result = Eligibility.ELIGIBLE;
            } else {
              result = Eligibility.WINDOW_CLOSED;
            }
          } else {
            result = Eligibility.TESTS_FAILED;
          }
        } else {
          result = Eligibility.NOT_APPROVED;
        }
      } else {
        result = Eligibility.INVALID_RELEASE_ID;
      }
    } else {
      result = Eligibility.MISSING_CANDIDATE;
    }
    return result;
  }
}
