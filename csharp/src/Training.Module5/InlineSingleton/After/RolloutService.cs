namespace Training.Module5.InlineSingleton.After;

public sealed class RolloutService
{
    private readonly FeatureFlags _flags;

    public RolloutService(FeatureFlags flags)
    {
        ArgumentNullException.ThrowIfNull(flags);
        _flags = flags;
    }

    public string Plan(string? releaseId)
    {
        if (string.IsNullOrWhiteSpace(releaseId))
        {
            throw new ArgumentException("releaseId must not be blank");
        }
        var stages = _flags.IsEnabled("canary") ? "10% -> 50% -> 100%" : "100%";
        var suffix = _flags.IsEnabled("dark-launch") ? " (dark launch)" : "";
        return releaseId + ": " + stages + suffix;
    }
}
