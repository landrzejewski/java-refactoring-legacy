namespace Training.Module5.Facade.After;

internal sealed class ReleaseNotifier
{
    private readonly Action<string> _effects;

    internal ReleaseNotifier(Action<string> effects)
    {
        ArgumentNullException.ThrowIfNull(effects);
        _effects = effects;
    }

    internal void Announce(string releaseId, string environment)
    {
        _effects("announced " + releaseId + " on " + environment);
    }
}
