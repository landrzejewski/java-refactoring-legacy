namespace Training.Module5.AntiCorruptionLayer.After;

/// <summary>
/// Reguły planowania wyrażone wyłącznie modelem domenowym.
/// </summary>
public sealed class DeploymentPlanner
{
    private readonly IEnvironmentDirectory _environments;

    public DeploymentPlanner(IEnvironmentDirectory environments)
    {
        ArgumentNullException.ThrowIfNull(environments);
        _environments = environments;
    }

    public IReadOnlyList<string> CanaryTargets() =>
        _environments.All()
            .Where(environment => environment.Active)
            .Where(environment => environment.Tier != Tier.Production)
            .Select(environment => environment.Name)
            .Order(StringComparer.Ordinal)
            .ToList();

    public IReadOnlyList<string> StaleEnvironments(DateOnly today)
    {
        var threshold = today.AddDays(-30);
        return _environments.All()
            .Where(environment => environment.Active)
            .Where(environment => environment.LastDeployment < threshold)
            .Select(environment => environment.Name)
            .Order(StringComparer.Ordinal)
            .ToList();
    }

    public bool IsProduction(string name) =>
        _environments.All().Any(environment => environment.Name == name
            && environment.Tier == Tier.Production);
}
