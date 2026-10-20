namespace Training.Module5.Facade.After;

internal sealed class ConfigRenderer
{
    private readonly Action<string> _effects;

    internal ConfigRenderer(Action<string> effects)
    {
        ArgumentNullException.ThrowIfNull(effects);
        _effects = effects;
    }

    internal string Render(string releaseId, string environment)
    {
        var config = releaseId + "/" + environment + ".yaml";
        _effects("rendered " + config);
        return config;
    }
}
