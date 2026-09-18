namespace Training.Module7.MethodObject;

public sealed record DeploymentRiskInput
{
    public DeploymentRiskInput(
        int changedFiles,
        int criticalServices,
        int failedChecks,
        bool rollbackTested)
    {
        RequireNonNegative(changedFiles, "changedFiles");
        RequireNonNegative(criticalServices, "criticalServices");
        RequireNonNegative(failedChecks, "failedChecks");
        ChangedFiles = changedFiles;
        CriticalServices = criticalServices;
        FailedChecks = failedChecks;
        RollbackTested = rollbackTested;
    }

    public int ChangedFiles { get; }

    public int CriticalServices { get; }

    public int FailedChecks { get; }

    public bool RollbackTested { get; }

    private static void RequireNonNegative(int value, string name)
    {
        if (value < 0)
        {
            throw new ArgumentException(name + " must not be negative");
        }
    }
}
