namespace Training.Module5.Facade.Before;

public sealed class DeploymentQueue
{
    private readonly Action<string> _effects;

    public DeploymentQueue(Action<string> effects)
    {
        ArgumentNullException.ThrowIfNull(effects);
        _effects = effects;
    }

    public void Enqueue(string artifact, string config)
    {
        _effects("queued " + artifact + " with " + config);
    }
}
