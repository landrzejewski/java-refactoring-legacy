package pl.training.module5.inlinesingleton.after;

/**
 * Korzeń kompozycji: jedyne miejsce, które decyduje, ile instancji flag istnieje.
 * "Jedna instancja" przestaje być własnością klasy, a staje się decyzją konfiguracji.
 */
public final class RolloutCompositionRoot {
    private RolloutCompositionRoot() {
    }

    public static RolloutService create(String flagsConfig) {
        return new RolloutService(FeatureFlags.parse(flagsConfig));
    }
}
