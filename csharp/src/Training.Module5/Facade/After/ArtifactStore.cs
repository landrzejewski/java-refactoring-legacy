namespace Training.Module5.Facade.After;

internal sealed class ArtifactStore
{
    private readonly Action<string> _effects;

    internal ArtifactStore(Action<string> effects)
    {
        ArgumentNullException.ThrowIfNull(effects);
        _effects = effects;
    }

    internal string Upload(string releaseId)
    {
        var artifact = releaseId + ".jar";
        _effects("stored " + artifact);
        return artifact;
    }
}
