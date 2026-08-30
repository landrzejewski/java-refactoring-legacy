package pl.training.module7.parameterobject.after;

import java.util.Objects;

public final class RolloutPlanner {
    private static final long DEPLOYMENT_SECONDS_PER_BATCH = 30;

    public long estimateSeconds(RolloutSpec spec) {
        Objects.requireNonNull(spec, "spec");

        long batches = Math.ceilDiv(
                (long) spec.instances(),
                spec.batchSize());
        long deploymentSeconds = Math.multiplyExact(
                batches,
                DEPLOYMENT_SECONDS_PER_BATCH);
        long pauseTime = Math.multiplyExact(
                batches - 1,
                (long) spec.pauseSeconds());
        return Math.addExact(deploymentSeconds, pauseTime);
    }

    public String describe(RolloutSpec spec) {
        Objects.requireNonNull(spec, "spec");

        return "service=" + spec.service()
                + ";region=" + spec.region()
                + ";instances=" + spec.instances()
                + ";batchSize=" + spec.batchSize()
                + ";pauseSeconds=" + spec.pauseSeconds();
    }
}
