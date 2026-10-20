package pl.training.module5.facade.before;

import java.util.Objects;
import java.util.function.Consumer;

public final class ReleaseNotifier {
    private final Consumer<String> effects;

    public ReleaseNotifier(Consumer<String> effects) {
        this.effects = Objects.requireNonNull(effects, "effects");
    }

    public void announce(String releaseId, String environment) {
        effects.accept("announced " + releaseId + " on " + environment);
    }
}
