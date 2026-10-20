package pl.training.module5.inlinesingleton.before;

/**
 * Ukryta zależność: z sygnatury nie widać, że wynik zależy od globalnych flag.
 */
public final class LegacyRolloutService {
    public String plan(String releaseId) {
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }
        LegacyFeatureFlags flags = LegacyFeatureFlags.getInstance();
        String stages = flags.isEnabled("canary") ? "10% -> 50% -> 100%" : "100%";
        String suffix = flags.isEnabled("dark-launch") ? " (dark launch)" : "";
        return releaseId + ": " + stages + suffix;
    }
}
