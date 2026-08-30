package pl.training.module8.risk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class RolloutThresholdsTest {
    @Test
    void acceptsInclusiveNumericBoundaries() {
        assertEquals(
                new RolloutThresholds(1, 0.0, 0.0),
                new RolloutThresholds(1, 0.0, 0.0));
        assertEquals(
                1.0,
                new RolloutThresholds(
                        Long.MAX_VALUE, 1.0, Double.MAX_VALUE)
                        .maximumErrorRate());
    }

    @Test
    void rejectsNonPositiveMinimumSampleSize() {
        for (long invalid : new long[] {Long.MIN_VALUE, -1, 0}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new RolloutThresholds(invalid, 0.05, 250.0));
        }
    }

    @Test
    void rejectsInvalidErrorRates() {
        double[] invalidValues = {
                Double.NEGATIVE_INFINITY,
                -Double.MIN_VALUE,
                -1.0,
                Math.nextUp(1.0),
                Double.POSITIVE_INFINITY,
                Double.NaN
        };

        for (double invalid : invalidValues) {
            assertThrows(IllegalArgumentException.class,
                    () -> new RolloutThresholds(100, invalid, 250.0));
        }
    }

    @Test
    void rejectsInvalidLatencyThresholds() {
        double[] invalidValues = {
                Double.NEGATIVE_INFINITY,
                -Double.MIN_VALUE,
                -1.0,
                Double.POSITIVE_INFINITY,
                Double.NaN
        };

        for (double invalid : invalidValues) {
            assertThrows(IllegalArgumentException.class,
                    () -> new RolloutThresholds(100, 0.05, invalid));
        }
    }
}
