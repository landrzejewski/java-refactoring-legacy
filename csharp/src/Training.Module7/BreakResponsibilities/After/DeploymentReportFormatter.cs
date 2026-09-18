using static System.FormattableString;

namespace Training.Module7.BreakResponsibilities.After;

public sealed class DeploymentReportFormatter
{
    public string Format(DeploymentMetrics metrics)
    {
        ArgumentNullException.ThrowIfNull(metrics);
        return Invariant(
            $"deployments={metrics.Deployments};failures={metrics.Failures};avgLeadTimeMinutes={metrics.AverageLeadTimeMinutes}");
    }
}
