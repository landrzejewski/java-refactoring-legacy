namespace Training.Module5.Facade.Before;

public sealed class ReleaseNotifier
{
    private readonly Action<string> _effects;

    public ReleaseNotifier(Action<string> effects)
    {
        ArgumentNullException.ThrowIfNull(effects);
        _effects = effects;
    }

    public void Announce(string releaseId, string environment)
    {
        _effects("announced " + releaseId + " on " + environment);
    }
}
