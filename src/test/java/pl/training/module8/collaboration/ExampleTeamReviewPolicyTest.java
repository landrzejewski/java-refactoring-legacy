package pl.training.module8.collaboration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

final class ExampleTeamReviewPolicyTest {
    private final ExampleTeamReviewPolicy policy = new ExampleTeamReviewPolicy();

    @Test
    void acceptsAFocusedChangeWithEvidenceAndAGreenBuild() {
        var changeSet = new ChangeSet(
                "Extract deployment clock",
                EnumSet.of(ChangeIntent.REFACTORING),
                List.of(new VerificationEvidence(
                        EvidenceKind.AUTOMATED_TEST,
                        "mvn test: 42 tests passed")),
                true);

        ReviewReadiness readiness = policy.assess(changeSet);

        assertTrue(readiness.ready());
        assertEquals(List.of(), readiness.problems());
    }

    @Test
    void reportsMixedIntentMissingEvidenceAndNonGreenBuildInStableOrder() {
        var changeSet = new ChangeSet(
                "Move validator and change its rules",
                EnumSet.of(
                        ChangeIntent.REFACTORING,
                        ChangeIntent.BEHAVIOR_CHANGE),
                List.of(),
                false);

        ReviewReadiness readiness = policy.assess(changeSet);

        assertFalse(readiness.ready());
        assertEquals(
                List.of(
                        ReadinessProblem.MIXED_PRIMARY_INTENTS,
                        ReadinessProblem.MISSING_VERIFICATION_EVIDENCE,
                        ReadinessProblem.BUILD_NOT_INDEPENDENTLY_GREEN),
                readiness.problems());
    }

    @Test
    void reportsAnUnspecifiedIntentAsATypedProblem() {
        var changeSet = new ChangeSet(
                "Unclassified change",
                EnumSet.noneOf(ChangeIntent.class),
                List.of(new VerificationEvidence(
                        EvidenceKind.STATIC_ANALYSIS,
                        "No new findings")),
                true);

        assertEquals(
                List.of(ReadinessProblem.MISSING_INTENT),
                policy.assess(changeSet).problems());
    }

    @Test
    void snapshotsMutableInputCollections() {
        var intents = EnumSet.of(ChangeIntent.CHARACTERIZATION_TESTS);
        var evidence = new ArrayList<>(List.of(new VerificationEvidence(
                EvidenceKind.AUTOMATED_TEST,
                "Characterization suite passed")));

        var changeSet = new ChangeSet("Capture behavior", intents, evidence, true);
        intents.add(ChangeIntent.ROLLOUT);
        evidence.clear();

        assertEquals(
                Set.of(ChangeIntent.CHARACTERIZATION_TESTS),
                changeSet.intents());
        assertEquals(1, changeSet.verificationEvidence().size());
        assertThrows(
                UnsupportedOperationException.class,
                () -> changeSet.intents().add(ChangeIntent.ROLLOUT));
        assertThrows(
                UnsupportedOperationException.class,
                () -> changeSet.verificationEvidence().clear());
    }
}
