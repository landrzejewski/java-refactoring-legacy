namespace Training.Module5.Facade.Before;

public sealed class ArtifactStore
{
    private readonly Action<string> _effects;

    public ArtifactStore(Action<string> effects)
    {
        ArgumentNullException.ThrowIfNull(effects);
        _effects = effects;
    }

    public string Upload(string releaseId)
    {
        var artifact = releaseId + ".jar";
        _effects("stored " + artifact);
        return artifact;
    }
}
