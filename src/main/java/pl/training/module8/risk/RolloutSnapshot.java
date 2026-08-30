package pl.training.module8.risk;

public record RolloutSnapshot(
        long sampleSize,
        long failedRequests,
        long mismatchedResponses,
        double p95LatencyMillis) {

    public RolloutSnapshot {
        if (sampleSize < 0) {
            throw new IllegalArgumentException(
                    "sampleSize must not be negative");
        }
        requireCountWithinSample(
                failedRequests, sampleSize, "failedRequests");
        requireCountWithinSample(
                mismatchedResponses, sampleSize, "mismatchedResponses");
        if (!Double.isFinite(p95LatencyMillis)
                || p95LatencyMillis < 0.0) {
            throw new IllegalArgumentException(
                    "p95LatencyMillis must be finite and not negative");
        }
        if (sampleSize == 0 && p95LatencyMillis != 0.0) {
            throw new IllegalArgumentException(
                    "an empty sample must have zero p95LatencyMillis");
        }
    }

    public double errorRate() {
        return sampleSize == 0
                ? 0.0
                : (double) failedRequests / sampleSize;
    }

    private static void requireCountWithinSample(
            long count,
            long sampleSize,
            String name) {
        if (count < 0 || count > sampleSize) {
            throw new IllegalArgumentException(
                    name + " must be between 0 and sampleSize");
        }
    }
}
