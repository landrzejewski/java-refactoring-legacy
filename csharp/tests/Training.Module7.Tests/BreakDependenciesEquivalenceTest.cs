using Training.Module7.BreakDependencies;
using Training.Module7.BreakDependencies.After;
using Training.Module7.BreakDependencies.Before;
using static Training.Module7.Tests.FailureAssertions;

namespace Training.Module7.Tests;

public sealed class BreakDependenciesEquivalenceTest
{
    [Fact]
    public void StandardDependencyPreservesDecisionsForEveryHour()
    {
        var before = new LegacyDeploymentWindowService();
        var after = new DeploymentWindowService(
            new BreakDependencies.After.StandardMaintenanceWindows());

        for (var hourUtc = 0; hourUtc < 24; hourUtc++)
        {
            Assert.True(
                before.Schedule("payments", hourUtc) == after.Schedule("payments", hourUtc),
                $"Different decision for hour {hourUtc}");
        }
    }

    [Fact]
    public void PreservesMaintenanceWindowBoundaries()
    {
        var service = new DeploymentWindowService(
            new BreakDependencies.After.StandardMaintenanceWindows());

        Assert.Equal(DeploymentDecision.Allowed, service.Schedule("payments", 0));
        Assert.Equal(DeploymentDecision.Allowed, service.Schedule("payments", 5));
        Assert.Equal(DeploymentDecision.OutsideMaintenanceWindow, service.Schedule("payments", 6));
        Assert.Equal(DeploymentDecision.OutsideMaintenanceWindow, service.Schedule("payments", 23));
    }

    [Fact]
    public void InjectedSeamControlsTheDecisionAndReceivesTheArgumentsOnce()
    {
        var calls = 0;
        string? receivedService = null;
        var receivedHour = -1;
        var service = new DeploymentWindowService(new LambdaMaintenanceWindows((candidate, hourUtc) =>
        {
            calls++;
            receivedService = candidate;
            receivedHour = hourUtc;
            return candidate == "emergency" && hourUtc == 14;
        }));

        Assert.Equal(DeploymentDecision.Allowed, service.Schedule("emergency", 14));
        Assert.Equal(1, calls);
        Assert.Equal("emergency", receivedService);
        Assert.Equal(14, receivedHour);
    }

    [Fact]
    public void PreservesValidationFailuresAndTheirOrder()
    {
        var before = new LegacyDeploymentWindowService();
        var after = new DeploymentWindowService(
            new BreakDependencies.After.StandardMaintenanceWindows());

        AssertSameFailure(
            () => before.Schedule(null!, 2),
            () => after.Schedule(null!, 2));
        AssertSameFailure(
            () => before.Schedule("   ", 2),
            () => after.Schedule("   ", 2));
        AssertSameFailure(
            () => before.Schedule("payments", -1),
            () => after.Schedule("payments", -1));
        AssertSameFailure(
            () => before.Schedule("payments", 24),
            () => after.Schedule("payments", 24));
        AssertSameFailure(
            () => before.Schedule(" ", -1),
            () => after.Schedule(" ", -1));
    }

    [Fact]
    public void InvalidRequestDoesNotReachInjectedDependency()
    {
        var calls = 0;
        var service = new DeploymentWindowService(new LambdaMaintenanceWindows((_, _) =>
        {
            calls++;
            return true;
        }));

        Assert.Throws<ArgumentNullException>(() => service.Schedule(null!, 2));
        Assert.Throws<ArgumentException>(() => service.Schedule(" ", 2));
        Assert.Throws<ArgumentException>(() => service.Schedule("payments", -1));
        Assert.Throws<ArgumentException>(() => service.Schedule("payments", 24));
        Assert.Throws<ArgumentException>(() => service.Schedule(" ", -1));
        Assert.Equal(0, calls);
    }

    [Fact]
    public void RejectsMissingInjectedDependency()
    {
        var failure = Assert.Throws<ArgumentNullException>(
            () => new DeploymentWindowService(null!));

        Assert.Equal("maintenanceWindows", failure.ParamName);
    }

    private sealed class LambdaMaintenanceWindows(Func<string, int, bool> allows) : IMaintenanceWindows
    {
        public bool Allows(string service, int hourUtc) => allows(service, hourUtc);
    }
}
