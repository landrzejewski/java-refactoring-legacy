using Training.Module7.ParameterObject.After;
using Training.Module7.ParameterObject.Before;
using static Training.Module7.Tests.FailureAssertions;

namespace Training.Module7.Tests;

public sealed class ParameterObjectEquivalenceTest
{
    [Fact]
    public void PreservesEstimationAndDescription()
    {
        var legacy = new LegacyRolloutPlanner();
        var refactored = new RolloutPlanner();
        var spec = new RolloutSpec("payments", "eu-central-1", 10, 3, 15);

        Assert.Equal(165, legacy.EstimateSeconds("payments", "eu-central-1", 10, 3, 15));
        Assert.Equal(
            legacy.EstimateSeconds("payments", "eu-central-1", 10, 3, 15),
            refactored.EstimateSeconds(spec));

        const string expected = "service=payments;region=eu-central-1;instances=10;"
            + "batchSize=3;pauseSeconds=15";
        Assert.Equal(expected, legacy.Describe("payments", "eu-central-1", 10, 3, 15));
        Assert.Equal(expected, refactored.Describe(spec));
    }

    [Fact]
    public void UsesOverflowSafeCeilingDivision()
    {
        var legacy = new LegacyRolloutPlanner();
        var refactored = new RolloutPlanner();
        var spec = new RolloutSpec("search", "eu-west-1", int.MaxValue, 2, 0);

        const long expected = 32_212_254_720L;
        Assert.Equal(
            expected,
            legacy.EstimateSeconds("search", "eu-west-1", int.MaxValue, 2, 0));
        Assert.Equal(expected, refactored.EstimateSeconds(spec));
    }

    [Fact]
    public void MovesValidationFromEveryOperationToParameterObjectConstruction()
    {
        var legacy = new LegacyRolloutPlanner();

        var legacyFailure = Assert.Throws<ArgumentException>(
            () => legacy.Describe("payments", "eu-central-1", 0, 0, -1));
        var constructionFailure = Assert.Throws<ArgumentException>(
            () => new RolloutSpec("payments", "eu-central-1", 0, 0, -1));

        Assert.Equal("instances must be greater than zero", legacyFailure.Message);
        Assert.Equal(legacyFailure.Message, constructionFailure.Message);
    }

    [Fact]
    public void PreservesValidationTypeMessageAndOrder()
    {
        var legacy = new LegacyRolloutPlanner();

        AssertSameFailure(
            () => legacy.Describe(null!, null!, 0, 0, -1),
            () => _ = new RolloutSpec(null!, null!, 0, 0, -1));
        AssertSameFailure(
            () => legacy.Describe(" ", null!, 0, 0, -1),
            () => _ = new RolloutSpec(" ", null!, 0, 0, -1));
        AssertSameFailure(
            () => legacy.Describe("api", null!, 0, 0, -1),
            () => _ = new RolloutSpec("api", null!, 0, 0, -1));
        AssertSameFailure(
            () => legacy.Describe("api", " ", 0, 0, -1),
            () => _ = new RolloutSpec("api", " ", 0, 0, -1));
        AssertSameFailure(
            () => legacy.Describe("api", "eu", 0, 0, -1),
            () => _ = new RolloutSpec("api", "eu", 0, 0, -1));
        AssertSameFailure(
            () => legacy.Describe("api", "eu", 1, 0, -1),
            () => _ = new RolloutSpec("api", "eu", 1, 0, -1));
        AssertSameFailure(
            () => legacy.Describe("api", "eu", 1, 1, -1),
            () => _ = new RolloutSpec("api", "eu", 1, 1, -1));
    }

    [Fact]
    public void RejectsNullParameterObjectAtTheNewApiBoundary()
    {
        var planner = new RolloutPlanner();

        var estimateFailure = Assert.Throws<ArgumentNullException>(
            () => planner.EstimateSeconds(null!));
        var describeFailure = Assert.Throws<ArgumentNullException>(
            () => planner.Describe(null!));

        Assert.Equal("spec", estimateFailure.ParamName);
        Assert.Equal("spec", describeFailure.ParamName);
    }
}
