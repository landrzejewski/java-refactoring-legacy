package pl.training.module7.parameterobject.before;

import java.util.Objects;

public final class LegacyRolloutPlanner {
    private static final long DEPLOYMENT_SECONDS_PER_BATCH = 30;

    public long estimateSeconds(
            String service,
            String region,
            int instances,
            int batchSize,
            int pauseSeconds) {

        validate(service, region, instances, batchSize, pauseSeconds);

        long batches = Math.ceilDiv((long) instances, batchSize);
        long deploymentSeconds = Math.multiplyExact(
                batches,
                DEPLOYMENT_SECONDS_PER_BATCH);
        long pauseTime = Math.multiplyExact(
                batches - 1,
                (long) pauseSeconds);
        return Math.addExact(deploymentSeconds, pauseTime);
    }

    public String describe(
            String service,
            String region,
            int instances,
            int batchSize,
            int pauseSeconds) {

        validate(service, region, instances, batchSize, pauseSeconds);

        return "service=" + service
                + ";region=" + region
                + ";instances=" + instances
                + ";batchSize=" + batchSize
                + ";pauseSeconds=" + pauseSeconds;
    }

    private void validate(
            String service,
            String region,
            int instances,
            int batchSize,
            int pauseSeconds) {

        Objects.requireNonNull(service, "service");
        if (service.isBlank()) {
            throw new IllegalArgumentException("service must not be blank");
        }
        Objects.requireNonNull(region, "region");
        if (region.isBlank()) {
            throw new IllegalArgumentException("region must not be blank");
        }
        if (instances <= 0) {
            throw new IllegalArgumentException(
                    "instances must be greater than zero");
        }
        if (batchSize <= 0) {
            throw new IllegalArgumentException(
                    "batchSize must be greater than zero");
        }
        if (pauseSeconds < 0) {
            throw new IllegalArgumentException(
                    "pauseSeconds must not be negative");
        }
    }
}
