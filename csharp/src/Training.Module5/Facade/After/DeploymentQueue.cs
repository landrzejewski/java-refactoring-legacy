namespace Training.Module5.Facade.After;

internal sealed class DeploymentQueue
{
    private readonly Action<string> _effects;

    internal DeploymentQueue(Action<string> effects)
    {
        ArgumentNullException.ThrowIfNull(effects);
        _effects = effects;
    }

    internal void Enqueue(string artifact, string config)
    {
        _effects("queued " + artifact + " with " + config);
    }
}
