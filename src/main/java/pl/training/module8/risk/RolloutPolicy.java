package pl.training.module8.risk;

import java.util.Objects;

public final class RolloutPolicy {
    private final RolloutThresholds thresholds;

    public RolloutPolicy(RolloutThresholds thresholds) {
        this.thresholds = Objects.requireNonNull(thresholds, "thresholds");
    }

    public RolloutDecision decide(RolloutSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");

        if (snapshot.mismatchedResponses() > 0
                || snapshot.errorRate() > thresholds.maximumErrorRate()
                || snapshot.p95LatencyMillis()
                        > thresholds.maximumP95LatencyMillis()) {
            return RolloutDecision.ROLLBACK;
        }
        if (snapshot.sampleSize() < thresholds.minimumSampleSize()) {
            return RolloutDecision.HOLD;
        }
        return RolloutDecision.ADVANCE;
    }
}
