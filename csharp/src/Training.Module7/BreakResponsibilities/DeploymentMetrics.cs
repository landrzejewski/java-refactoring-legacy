namespace Training.Module7.BreakResponsibilities;

public sealed record DeploymentMetrics
{
    public DeploymentMetrics(int deployments, int failures, long averageLeadTimeMinutes)
    {
        if (deployments < 0)
        {
            throw new ArgumentException("deployments must not be negative");
        }
        if (failures < 0 || failures > deployments)
        {
            throw new ArgumentException("failures must be between 0 and deployments");
        }
        if (averageLeadTimeMinutes < 0)
        {
            throw new ArgumentException("averageLeadTimeMinutes must not be negative");
        }
        if (deployments == 0 && averageLeadTimeMinutes != 0)
        {
            throw new ArgumentException("empty metrics must have zero average lead time");
        }
        Deployments = deployments;
        Failures = failures;
        AverageLeadTimeMinutes = averageLeadTimeMinutes;
    }

    public int Deployments { get; }

    public int Failures { get; }

    public long AverageLeadTimeMinutes { get; }
}
