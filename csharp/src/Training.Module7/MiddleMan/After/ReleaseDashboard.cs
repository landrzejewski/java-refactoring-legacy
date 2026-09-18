namespace Training.Module7.MiddleMan.After;

public sealed class ReleaseDashboard
{
    private readonly DeploymentRegistry _registry;

    public ReleaseDashboard(DeploymentRegistry registry)
    {
        ArgumentNullException.ThrowIfNull(registry);
        _registry = registry;
    }

    public string Render(string deploymentId) =>
        deploymentId + " -> " + _registry.StatusOf(deploymentId).ToString().ToUpperInvariant();
}
