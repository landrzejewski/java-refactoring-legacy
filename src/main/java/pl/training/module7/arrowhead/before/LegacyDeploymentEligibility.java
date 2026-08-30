package pl.training.module7.arrowhead.before;

import pl.training.module7.arrowhead.DeploymentCandidate;
import pl.training.module7.arrowhead.Eligibility;

public final class LegacyDeploymentEligibility {
    public Eligibility evaluate(DeploymentCandidate candidate) {
        Eligibility result;
        if (candidate != null) {
            if (candidate.releaseId() != null
                    && !candidate.releaseId().isBlank()) {
                if (candidate.approved()) {
                    if (candidate.testsPassed()) {
                        if (candidate.windowOpen()) {
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
