using Training.Module7.BreakResponsibilities;
using Training.Module7.BreakResponsibilities.After;
using Training.Module7.BreakResponsibilities.Before;
using static Training.Module7.Tests.FailureAssertions;

namespace Training.Module7.Tests;

public sealed class BreakResponsibilitiesEquivalenceTest
{
    [Fact]
    public void PreservesGeneratedReport()
    {
        DeploymentSample[] samples =
        [
            new(12, true),
            new(18, false),
            new(24, true)
        ];
        var before = new LegacyDeploymentReport();
        var after = Service();

        Assert.Equal(
            "deployments=3;failures=1;avgLeadTimeMinutes=18",
            before.Generate(samples));
        Assert.Equal(before.Generate(samples), after.Generate(samples));
    }

    [Fact]
    public void PreservesEmptyReportAndIntegerAverage()
    {
        var before = new LegacyDeploymentReport();
        var after = Service();

        Assert.Equal(
            "deployments=0;failures=0;avgLeadTimeMinutes=0",
            before.Generate([]));
        Assert.Equal(before.Generate([]), after.Generate([]));

        DeploymentSample[] samples =
        [
            new(1, true),
            new(2, true)
        ];
        Assert.Equal(
            "deployments=2;failures=0;avgLeadTimeMinutes=1",
            before.Generate(samples));
        Assert.Equal(before.Generate(samples), after.Generate(samples));
    }

    [Fact]
    public void NeitherImplementationMutatesTheInputList()
    {
        var samples = new List<DeploymentSample>
        {
            new(30, false),
            new(10, true)
        };
        var snapshot = samples.ToList();

        new LegacyDeploymentReport().Generate(samples);
        Assert.Equal(snapshot, samples);

        Service().Generate(samples);
        Assert.Equal(snapshot, samples);
    }

    [Fact]
    public void PreservesFailuresForMissingListAndElement()
    {
        var before = new LegacyDeploymentReport();
        var after = Service();

        AssertSameFailure(
            () => before.Generate(null!),
            () => after.Generate(null!));

        DeploymentSample[] containingNull = [new(10, true), null!];
        AssertSameFailure(
            () => before.Generate(containingNull),
            () => after.Generate(containingNull));
    }

    [Fact]
    public void PreservesOverflowFailureWhileAccumulatingLeadTime()
    {
        DeploymentSample[] samples =
        [
            new(long.MaxValue, true),
            new(1, false)
        ];
        var before = new LegacyDeploymentReport();
        var after = Service();

        AssertSameFailure(
            () => before.Generate(samples),
            () => after.Generate(samples));
        Assert.Throws<OverflowException>(() => after.Generate(samples));
    }

    [Fact]
    public void ExtractedComponentsHaveFocusedContracts()
    {
        var calculator = new DeploymentMetricsCalculator();
        var formatter = new DeploymentReportFormatter();
        DeploymentSample[] samples =
        [
            new(15, true),
            new(25, false)
        ];

        var metrics = calculator.Calculate(samples);

        Assert.Equal(new DeploymentMetrics(2, 1, 20), metrics);
        Assert.Equal(
            "deployments=2;failures=1;avgLeadTimeMinutes=20",
            formatter.Format(metrics));
    }

    [Fact]
    public void DomainValuesAndCollaboratorsRejectInvalidState()
    {
        var invalidSample = Assert.Throws<ArgumentException>(
            () => new DeploymentSample(-1, true));
        var invalidFailureCount = Assert.Throws<ArgumentException>(
            () => new DeploymentMetrics(1, 2, 10));
        var invalidEmptyAverage = Assert.Throws<ArgumentException>(
            () => new DeploymentMetrics(0, 0, 1));
        var missingMetrics = Assert.Throws<ArgumentNullException>(
            () => new DeploymentReportFormatter().Format(null!));
        var missingCalculator = Assert.Throws<ArgumentNullException>(
            () => new DeploymentReportService(null!, new DeploymentReportFormatter()));
        var missingFormatter = Assert.Throws<ArgumentNullException>(
            () => new DeploymentReportService(new DeploymentMetricsCalculator(), null!));

        Assert.Equal("leadTimeMinutes must not be negative", invalidSample.Message);
        Assert.Equal("failures must be between 0 and deployments", invalidFailureCount.Message);
        Assert.Equal("empty metrics must have zero average lead time", invalidEmptyAverage.Message);
        Assert.Equal("metrics", missingMetrics.ParamName);
        Assert.Equal("calculator", missingCalculator.ParamName);
        Assert.Equal("formatter", missingFormatter.ParamName);
    }

    private static DeploymentReportService Service() =>
        new(new DeploymentMetricsCalculator(), new DeploymentReportFormatter());
}
