package pl.training.module8.risk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class RolloutPolicyTest {
    private final RolloutPolicy policy = new RolloutPolicy(
            new RolloutThresholds(100, 0.05, 250.0));

    @Test
    void advancesWhenTheSampleAndMetricsMeetAllThresholds() {
        assertEquals(
                RolloutDecision.ADVANCE,
                policy.decide(new RolloutSnapshot(200, 4, 0, 180.0)));
    }

    @Test
    void advancesAtInclusiveMetricAndSampleBoundaries() {
        assertEquals(
                RolloutDecision.ADVANCE,
                policy.decide(new RolloutSnapshot(100, 5, 0, 250.0)));
    }

    @Test
    void holdsWhenAHealthySampleIsStillTooSmall() {
        assertEquals(
                RolloutDecision.HOLD,
                policy.decide(new RolloutSnapshot(99, 0, 0, 120.0)));
        assertEquals(
                RolloutDecision.HOLD,
                policy.decide(new RolloutSnapshot(0, 0, 0, 0.0)));
    }

    @Test
    void rollsBackAfterAnyBehaviorMismatch() {
        assertEquals(
                RolloutDecision.ROLLBACK,
                policy.decide(new RolloutSnapshot(200, 0, 1, 120.0)));
    }

    @Test
    void rollsBackAfterAnAbsoluteErrorOrLatencySloViolation() {
        assertEquals(
                RolloutDecision.ROLLBACK,
                policy.decide(new RolloutSnapshot(100, 6, 0, 200.0)));
        assertEquals(
                RolloutDecision.ROLLBACK,
                policy.decide(new RolloutSnapshot(100, 0, 0, 250.01)));
    }

    @Test
    void safetyViolationsTakePriorityOverAnInsufficientSample() {
        assertEquals(
                RolloutDecision.ROLLBACK,
                policy.decide(new RolloutSnapshot(10, 0, 1, 100.0)));
        assertEquals(
                RolloutDecision.ROLLBACK,
                policy.decide(new RolloutSnapshot(10, 1, 0, 100.0)));
        assertEquals(
                RolloutDecision.ROLLBACK,
                policy.decide(new RolloutSnapshot(10, 0, 0, 251.0)));
    }

    @Test
    void rejectsMissingPolicyInputs() {
        assertThrows(NullPointerException.class,
                () -> new RolloutPolicy(null));
        assertThrows(NullPointerException.class,
                () -> policy.decide(null));
    }
}
