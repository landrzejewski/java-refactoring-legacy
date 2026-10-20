package pl.training.module5.inlinesingleton.after;

import java.util.Arrays;
import java.util.Set;

/**
 * Inline Singleton: zwykły, niezmienny obiekt wartości zamiast globalnej instancji.
 * Tworzony w korzeniu kompozycji i przekazywany przez konstruktor, więc zależność
 * jest jawna, a każdy test może mieć własny zestaw flag bez sprzątania globalnego stanu.
 */
public record FeatureFlags(Set<String> enabled) {
    public FeatureFlags {
        enabled = Set.copyOf(enabled);
    }

    public static FeatureFlags of(String... flags) {
        return new FeatureFlags(Set.copyOf(Arrays.asList(flags)));
    }

    /**
     * Konfiguracja w postaci {@code "canary,dark-launch"}; pusty tekst oznacza brak flag.
     */
    public static FeatureFlags parse(String config) {
        if (config == null || config.isBlank()) {
            return of();
        }
        return new FeatureFlags(Set.copyOf(Arrays.stream(config.split(","))
                .map(String::trim)
                .filter(flag -> !flag.isEmpty())
                .toList()));
    }

    public boolean isEnabled(String flag) {
        return enabled.contains(flag);
    }
}
