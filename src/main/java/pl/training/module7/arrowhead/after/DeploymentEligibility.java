package pl.training.module7.arrowhead.after;

import pl.training.module7.arrowhead.DeploymentCandidate;
import pl.training.module7.arrowhead.Eligibility;

public final class DeploymentEligibility {
    public Eligibility evaluate(DeploymentCandidate candidate) {
        if (candidate == null) {
            return Eligibility.MISSING_CANDIDATE;
        }
        if (candidate.releaseId() == null
                || candidate.releaseId().isBlank()) {
            return Eligibility.INVALID_RELEASE_ID;
        }
        if (!candidate.approved()) {
            return Eligibility.NOT_APPROVED;
        }
        if (!candidate.testsPassed()) {
            return Eligibility.TESTS_FAILED;
        }
        if (!candidate.windowOpen()) {
            return Eligibility.WINDOW_CLOSED;
        }
        return Eligibility.ELIGIBLE;
    }
}
