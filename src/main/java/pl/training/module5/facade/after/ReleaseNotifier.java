package pl.training.module5.facade.after;

import java.util.Objects;
import java.util.function.Consumer;

final class ReleaseNotifier {
    private final Consumer<String> effects;

    ReleaseNotifier(Consumer<String> effects) {
        this.effects = Objects.requireNonNull(effects, "effects");
    }

    void announce(String releaseId, String environment) {
        effects.accept("announced " + releaseId + " on " + environment);
    }
}
