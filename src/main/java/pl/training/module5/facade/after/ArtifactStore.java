package pl.training.module5.facade.after;

import java.util.Objects;
import java.util.function.Consumer;

final class ArtifactStore {
    private final Consumer<String> effects;

    ArtifactStore(Consumer<String> effects) {
        this.effects = Objects.requireNonNull(effects, "effects");
    }

    String upload(String releaseId) {
        String artifact = releaseId + ".jar";
        effects.accept("stored " + artifact);
        return artifact;
    }
}
