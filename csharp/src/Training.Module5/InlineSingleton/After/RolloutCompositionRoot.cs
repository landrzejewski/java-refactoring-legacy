namespace Training.Module5.InlineSingleton.After;

/// <summary>
/// Korzeń kompozycji: jedyne miejsce, które decyduje, ile instancji flag istnieje.
/// "Jedna instancja" przestaje być własnością klasy, a staje się decyzją konfiguracji.
/// </summary>
public static class RolloutCompositionRoot
{
    public static RolloutService Create(string? flagsConfig) =>
        new(FeatureFlags.Parse(flagsConfig));
}
