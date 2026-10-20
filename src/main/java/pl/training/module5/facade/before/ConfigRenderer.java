package pl.training.module5.facade.before;

import java.util.Objects;
import java.util.function.Consumer;

public final class ConfigRenderer {
    private final Consumer<String> effects;

    public ConfigRenderer(Consumer<String> effects) {
        this.effects = Objects.requireNonNull(effects, "effects");
    }

    public String render(String releaseId, String environment) {
        String config = releaseId + "/" + environment + ".yaml";
        effects.accept("rendered " + config);
        return config;
    }
}
