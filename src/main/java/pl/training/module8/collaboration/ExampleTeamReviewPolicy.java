package pl.training.module8.collaboration;

import java.util.ArrayList;
import java.util.Objects;

/**
 * An example team policy, not a universal definition of review readiness.
 */
public final class ExampleTeamReviewPolicy {
    public ReviewReadiness assess(ChangeSet changeSet) {
        Objects.requireNonNull(changeSet, "changeSet");

        var problems = new ArrayList<ReadinessProblem>();
        if (changeSet.intents().isEmpty()) {
            problems.add(ReadinessProblem.MISSING_INTENT);
        } else if (changeSet.intents().size() > 1) {
            problems.add(ReadinessProblem.MIXED_PRIMARY_INTENTS);
        }
        if (changeSet.verificationEvidence().isEmpty()) {
            problems.add(ReadinessProblem.MISSING_VERIFICATION_EVIDENCE);
        }
        if (!changeSet.independentlyGreenBuild()) {
            problems.add(ReadinessProblem.BUILD_NOT_INDEPENDENTLY_GREEN);
        }
        return new ReviewReadiness(problems);
    }
}
