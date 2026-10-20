package pl.training.module5.inlinesingleton.before;

import java.util.HashSet;
import java.util.Set;

/**
 * Globalny, mutowalny singleton. Każdy może go zmienić z dowolnego miejsca, a klasy,
 * które z niego korzystają, nie pokazują tej zależności w konstruktorze.
 */
public final class LegacyFeatureFlags {
    private static final LegacyFeatureFlags INSTANCE = new LegacyFeatureFlags();

    private final Set<String> enabled = new HashSet<>();

    private LegacyFeatureFlags() {
    }

    public static LegacyFeatureFlags getInstance() {
        return INSTANCE;
    }

    public synchronized void enable(String flag) {
        enabled.add(flag);
    }

    public synchronized void disable(String flag) {
        enabled.remove(flag);
    }

    public synchronized boolean isEnabled(String flag) {
        return enabled.contains(flag);
    }

    // istnieje tylko po to, żeby testy mogły posprzątać globalny stan
    public synchronized void reset() {
        enabled.clear();
    }
}
