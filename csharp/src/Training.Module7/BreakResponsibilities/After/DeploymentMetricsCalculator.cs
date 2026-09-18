namespace Training.Module7.BreakResponsibilities.After;

public sealed class DeploymentMetricsCalculator
{
    public DeploymentMetrics Calculate(IEnumerable<DeploymentSample> samples)
    {
        ArgumentNullException.ThrowIfNull(samples);

        var deployments = 0;
        var failures = 0;
        long totalLeadTimeMinutes = 0;

        foreach (var sample in samples)
        {
            ArgumentNullException.ThrowIfNull(sample);
            deployments++;
            if (!sample.Successful)
            {
                failures++;
            }
            totalLeadTimeMinutes = checked(totalLeadTimeMinutes + sample.LeadTimeMinutes);
        }

        var averageLeadTimeMinutes = deployments == 0
            ? 0
            : totalLeadTimeMinutes / deployments;

        return new DeploymentMetrics(deployments, failures, averageLeadTimeMinutes);
    }
}
