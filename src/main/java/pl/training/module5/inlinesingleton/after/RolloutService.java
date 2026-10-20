package pl.training.module5.inlinesingleton.after;

import java.util.Objects;

public final class RolloutService {
    private final FeatureFlags flags;

    public RolloutService(FeatureFlags flags) {
        this.flags = Objects.requireNonNull(flags, "flags");
    }

    public String plan(String releaseId) {
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }
        String stages = flags.isEnabled("canary") ? "10% -> 50% -> 100%" : "100%";
        String suffix = flags.isEnabled("dark-launch") ? " (dark launch)" : "";
        return releaseId + ": " + stages + suffix;
    }
}
