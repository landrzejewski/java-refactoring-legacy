namespace Training.Module5.Facade.Before;

public sealed class ConfigRenderer
{
    private readonly Action<string> _effects;

    public ConfigRenderer(Action<string> effects)
    {
        ArgumentNullException.ThrowIfNull(effects);
        _effects = effects;
    }

    public string Render(string releaseId, string environment)
    {
        var config = releaseId + "/" + environment + ".yaml";
        _effects("rendered " + config);
        return config;
    }
}
