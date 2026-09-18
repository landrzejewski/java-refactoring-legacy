using Training.Module8.Risk;

namespace Training.Module8.Tests.Risk;

public sealed class RolloutPolicyTest
{
    private readonly RolloutPolicy policy = new(
        new RolloutThresholds(100, 0.05, 250.0));

    [Fact]
    public void AdvancesWhenTheSampleAndMetricsMeetAllThresholds()
    {
        Assert.Equal(
            RolloutDecision.Advance,
            policy.Decide(new RolloutSnapshot(200, 4, 0, 180.0)));
    }

    [Fact]
    public void AdvancesAtInclusiveMetricAndSampleBoundaries()
    {
        Assert.Equal(
            RolloutDecision.Advance,
            policy.Decide(new RolloutSnapshot(100, 5, 0, 250.0)));
    }

    [Fact]
    public void HoldsWhenAHealthySampleIsStillTooSmall()
    {
        Assert.Equal(
            RolloutDecision.Hold,
            policy.Decide(new RolloutSnapshot(99, 0, 0, 120.0)));
        Assert.Equal(
            RolloutDecision.Hold,
            policy.Decide(new RolloutSnapshot(0, 0, 0, 0.0)));
    }

    [Fact]
    public void RollsBackAfterAnyBehaviorMismatch()
    {
        Assert.Equal(
            RolloutDecision.Rollback,
            policy.Decide(new RolloutSnapshot(200, 0, 1, 120.0)));
    }

    [Fact]
    public void RollsBackAfterAnAbsoluteErrorOrLatencySloViolation()
    {
        Assert.Equal(
            RolloutDecision.Rollback,
            policy.Decide(new RolloutSnapshot(100, 6, 0, 200.0)));
        Assert.Equal(
            RolloutDecision.Rollback,
            policy.Decide(new RolloutSnapshot(100, 0, 0, 250.01)));
    }

    [Fact]
    public void SafetyViolationsTakePriorityOverAnInsufficientSample()
    {
        Assert.Equal(
            RolloutDecision.Rollback,
            policy.Decide(new RolloutSnapshot(10, 0, 1, 100.0)));
        Assert.Equal(
            RolloutDecision.Rollback,
            policy.Decide(new RolloutSnapshot(10, 1, 0, 100.0)));
        Assert.Equal(
            RolloutDecision.Rollback,
            policy.Decide(new RolloutSnapshot(10, 0, 0, 251.0)));
    }

    [Fact]
    public void RejectsMissingPolicyInputs()
    {
        Assert.Throws<ArgumentNullException>(
            () => new RolloutPolicy(null!));
        Assert.Throws<ArgumentNullException>(
            () => policy.Decide(null!));
    }
}
