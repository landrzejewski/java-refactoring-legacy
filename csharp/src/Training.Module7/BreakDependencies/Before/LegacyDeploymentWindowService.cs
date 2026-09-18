namespace Training.Module7.BreakDependencies.Before;

public sealed class LegacyDeploymentWindowService
{
    // Hard-wired concrete dependency: no seam for tests or alternative policies.
    private readonly StandardMaintenanceWindows _maintenanceWindows = new();

    public DeploymentDecision Schedule(string service, int hourUtc)
    {
        Validate(service, hourUtc);
        return _maintenanceWindows.Allows(service, hourUtc)
            ? DeploymentDecision.Allowed
            : DeploymentDecision.OutsideMaintenanceWindow;
    }

    private static void Validate(string service, int hourUtc)
    {
        ArgumentNullException.ThrowIfNull(service);
        if (string.IsNullOrWhiteSpace(service))
        {
            throw new ArgumentException("service must not be blank");
        }
        if (hourUtc < 0 || hourUtc > 23)
        {
            throw new ArgumentException("hourUtc must be between 0 and 23");
        }
    }
}
