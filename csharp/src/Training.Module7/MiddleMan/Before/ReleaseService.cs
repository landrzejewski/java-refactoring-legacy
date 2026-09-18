namespace Training.Module7.MiddleMan.Before;

// Middle man: every method only delegates to the registry.
public sealed class ReleaseService
{
    private readonly DeploymentRegistry _registry;

    public ReleaseService(DeploymentRegistry registry)
    {
        ArgumentNullException.ThrowIfNull(registry);
        _registry = registry;
    }

    public void Update(string deploymentId, DeploymentStatus status) =>
        _registry.Update(deploymentId, status);

    public DeploymentStatus StatusOf(string deploymentId) =>
        _registry.StatusOf(deploymentId);
}
