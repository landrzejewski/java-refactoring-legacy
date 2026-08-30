package pl.training.module8.risk;

public record RolloutThresholds(
        long minimumSampleSize,
        double maximumErrorRate,
        double maximumP95LatencyMillis) {

    public RolloutThresholds {
        if (minimumSampleSize < 1) {
            throw new IllegalArgumentException(
                    "minimumSampleSize must be positive");
        }
        requireFiniteInRange(
                maximumErrorRate, 0.0, 1.0, "maximumErrorRate");
        requireFiniteInRange(
                maximumP95LatencyMillis,
                0.0,
                Double.MAX_VALUE,
                "maximumP95LatencyMillis");
    }

    private static void requireFiniteInRange(
            double value,
            double minimum,
            double maximum,
            String name) {
        if (!Double.isFinite(value) || value < minimum || value > maximum) {
            throw new IllegalArgumentException(
                    name + " must be finite and between "
                            + minimum + " and " + maximum);
        }
    }
}
