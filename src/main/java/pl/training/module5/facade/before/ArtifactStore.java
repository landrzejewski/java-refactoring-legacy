package pl.training.module5.facade.before;

import java.util.Objects;
import java.util.function.Consumer;

public final class ArtifactStore {
    private final Consumer<String> effects;

    public ArtifactStore(Consumer<String> effects) {
        this.effects = Objects.requireNonNull(effects, "effects");
    }

    public String upload(String releaseId) {
        String artifact = releaseId + ".jar";
        effects.accept("stored " + artifact);
        return artifact;
    }
}
