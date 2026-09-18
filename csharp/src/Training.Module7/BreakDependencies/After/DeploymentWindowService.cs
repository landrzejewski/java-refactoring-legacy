namespace Training.Module7.BreakDependencies.After;

public sealed class DeploymentWindowService
{
    private readonly IMaintenanceWindows _maintenanceWindows;

    public DeploymentWindowService(IMaintenanceWindows maintenanceWindows)
    {
        ArgumentNullException.ThrowIfNull(maintenanceWindows);
        _maintenanceWindows = maintenanceWindows;
    }

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
        if (hourUtc is < 0 or > 23)
        {
            throw new ArgumentException("hourUtc must be between 0 and 23");
        }
    }
}
