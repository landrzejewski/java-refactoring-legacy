package pl.training.module5.facade.before;

import java.util.Objects;
import java.util.function.Consumer;

public final class DeploymentQueue {
    private final Consumer<String> effects;

    public DeploymentQueue(Consumer<String> effects) {
        this.effects = Objects.requireNonNull(effects, "effects");
    }

    public void enqueue(String artifact, String config) {
        effects.accept("queued " + artifact + " with " + config);
    }
}
