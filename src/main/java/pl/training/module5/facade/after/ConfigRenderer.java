package pl.training.module5.facade.after;

import java.util.Objects;
import java.util.function.Consumer;

final class ConfigRenderer {
    private final Consumer<String> effects;

    ConfigRenderer(Consumer<String> effects) {
        this.effects = Objects.requireNonNull(effects, "effects");
    }

    String render(String releaseId, String environment) {
        String config = releaseId + "/" + environment + ".yaml";
        effects.accept("rendered " + config);
        return config;
    }
}
