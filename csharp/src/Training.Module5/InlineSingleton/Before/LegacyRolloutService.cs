namespace Training.Module5.InlineSingleton.Before;

/// <summary>
/// Ukryta zależność: z sygnatury nie widać, że wynik zależy od globalnych flag.
/// </summary>
public sealed class LegacyRolloutService
{
    public string Plan(string? releaseId)
    {
        if (string.IsNullOrWhiteSpace(releaseId))
        {
            throw new ArgumentException("releaseId must not be blank");
        }
        var flags = LegacyFeatureFlags.Instance;
        var stages = flags.IsEnabled("canary") ? "10% -> 50% -> 100%" : "100%";
        var suffix = flags.IsEnabled("dark-launch") ? " (dark launch)" : "";
        return releaseId + ": " + stages + suffix;
    }
}
