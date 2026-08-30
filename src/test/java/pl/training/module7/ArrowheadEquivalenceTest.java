package pl.training.module7;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import pl.training.module7.arrowhead.DeploymentCandidate;
import pl.training.module7.arrowhead.Eligibility;
import pl.training.module7.arrowhead.after.DeploymentEligibility;
import pl.training.module7.arrowhead.before.LegacyDeploymentEligibility;

final class ArrowheadEquivalenceTest {
    private final LegacyDeploymentEligibility before =
            new LegacyDeploymentEligibility();
    private final DeploymentEligibility after = new DeploymentEligibility();

    @Test
    void guardClausesPreserveTheCompleteDecisionMatrix() {
        boolean[] values = {false, true};

        for (boolean approved : values) {
            for (boolean testsPassed : values) {
                for (boolean windowOpen : values) {
                    var candidate = new DeploymentCandidate(
                            "rel-42", approved, testsPassed, windowOpen);
                    Eligibility expected = !approved
                            ? Eligibility.NOT_APPROVED
                            : !testsPassed
                                    ? Eligibility.TESTS_FAILED
                                    : !windowOpen
                                            ? Eligibility.WINDOW_CLOSED
                                            : Eligibility.ELIGIBLE;

                    assertEquals(expected, before.evaluate(candidate));
                    assertEquals(expected, after.evaluate(candidate));
                }
            }
        }
    }

    @Test
    void guardClausesPreserveValidationAndFailurePriority() {
        assertSameDecision(null, Eligibility.MISSING_CANDIDATE);
        assertSameDecision(
                new DeploymentCandidate(null, false, false, false),
                Eligibility.INVALID_RELEASE_ID);
        assertSameDecision(
                new DeploymentCandidate(" \t", false, false, false),
                Eligibility.INVALID_RELEASE_ID);
        assertSameDecision(
                new DeploymentCandidate("rel-42", false, false, false),
                Eligibility.NOT_APPROVED);
        assertSameDecision(
                new DeploymentCandidate("rel-42", true, false, false),
                Eligibility.TESTS_FAILED);
        assertSameDecision(
                new DeploymentCandidate("rel-42", true, true, false),
                Eligibility.WINDOW_CLOSED);
    }

    private void assertSameDecision(
            DeploymentCandidate candidate,
            Eligibility expected) {
        assertEquals(expected, before.evaluate(candidate));
        assertEquals(expected, after.evaluate(candidate));
        assertEquals(before.evaluate(candidate), after.evaluate(candidate));
    }
}
