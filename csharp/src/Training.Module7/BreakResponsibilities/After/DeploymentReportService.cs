namespace Training.Module7.BreakResponsibilities.After;

public sealed class DeploymentReportService
{
    private readonly DeploymentMetricsCalculator _calculator;
    private readonly DeploymentReportFormatter _formatter;

    public DeploymentReportService(
        DeploymentMetricsCalculator calculator,
        DeploymentReportFormatter formatter)
    {
        ArgumentNullException.ThrowIfNull(calculator);
        ArgumentNullException.ThrowIfNull(formatter);
        _calculator = calculator;
        _formatter = formatter;
    }

    public string Generate(IEnumerable<DeploymentSample> samples) =>
        _formatter.Format(_calculator.Calculate(samples));
}
