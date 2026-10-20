package pl.training.module5.facade.after;

import java.util.Objects;
import java.util.function.Consumer;

final class DeploymentQueue {
    private final Consumer<String> effects;

    DeploymentQueue(Consumer<String> effects) {
        this.effects = Objects.requireNonNull(effects, "effects");
    }

    void enqueue(String artifact, String config) {
        effects.accept("queued " + artifact + " with " + config);
    }
}
