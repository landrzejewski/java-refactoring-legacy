package pl.training.module8.risk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class RolloutSnapshotTest {
    @Test
    void calculatesErrorRateAndDefinesItForAnEmptySample() {
        assertEquals(
                0.25,
                new RolloutSnapshot(20, 5, 0, 100.0).errorRate());
        assertEquals(
                0.0,
                new RolloutSnapshot(0, 0, 0, 0.0).errorRate());
    }

    @Test
    void acceptsCountsAtBothBoundaries() {
        new RolloutSnapshot(1, 0, 0, 0.0);
        new RolloutSnapshot(1, 1, 1, Double.MAX_VALUE);
    }

    @Test
    void rejectsNegativeOrInconsistentCounts() {
        assertThrows(IllegalArgumentException.class,
                () -> new RolloutSnapshot(-1, 0, 0, 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> new RolloutSnapshot(10, -1, 0, 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> new RolloutSnapshot(10, 11, 0, 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> new RolloutSnapshot(10, 0, -1, 0.0));
        assertThrows(IllegalArgumentException.class,
                () -> new RolloutSnapshot(10, 0, 11, 0.0));
    }

    @Test
    void rejectsInvalidLatencyMeasurements() {
        double[] invalidValues = {
                Double.NEGATIVE_INFINITY,
                -Double.MIN_VALUE,
                -1.0,
                Double.POSITIVE_INFINITY,
                Double.NaN
        };

        for (double invalid : invalidValues) {
            assertThrows(IllegalArgumentException.class,
                    () -> new RolloutSnapshot(10, 0, 0, invalid));
        }
    }

    @Test
    void requiresZeroLatencyForAnEmptySample() {
        assertThrows(IllegalArgumentException.class,
                () -> new RolloutSnapshot(0, 0, 0, Double.MIN_VALUE));
    }
}
