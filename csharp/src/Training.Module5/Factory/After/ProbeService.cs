namespace Training.Module5.Factory.After;

public sealed class ProbeService
{
    private readonly DeploymentProbeFactory _factory;

    public ProbeService(DeploymentProbeFactory factory)
    {
        ArgumentNullException.ThrowIfNull(factory);
        _factory = factory;
    }

    public string Check(DeploymentProbeFactory.ProbeKind kind, string? target) =>
        _factory.Create(kind, target).Check();
}
