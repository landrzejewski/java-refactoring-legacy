package pl.training.module7.parameterobject.after;

import java.util.Objects;

public record RolloutSpec(
        String service,
        String region,
        int instances,
        int batchSize,
        int pauseSeconds) {

    public RolloutSpec {
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
