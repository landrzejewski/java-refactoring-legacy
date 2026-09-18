using Training.Module6.Strategy.After;
using Training.Module6.Strategy.Before;
using DeploymentMode = Training.Module6.Strategy.Before.LegacyDeploymentCostCalculator.DeploymentMode;

namespace Training.Module6.Tests;

public sealed class StrategyEquivalenceTest
{
    [Fact]
    public void PreservesEveryLegacyCalculationVariant()
    {
        var legacy = new LegacyDeploymentCostCalculator();

        foreach (long baseCost in new long[] { 0, 1, 4, 10_001 })
        {
            Assert.Equal(
                legacy.Calculate(baseCost, DeploymentMode.Standard),
                new DeploymentCostCalculator(new StandardCostPolicy()).Calculate(baseCost));
            Assert.Equal(
                legacy.Calculate(baseCost, DeploymentMode.Expedited),
                new DeploymentCostCalculator(new ExpeditedCostPolicy()).Calculate(baseCost));
        }
    }

    [Fact]
    public void KeepsInputValidationInTheContext()
    {
        var calculator = new DeploymentCostCalculator(new ZeroCostPolicy());

        var exception = Assert.Throws<ArgumentException>(() => calculator.Calculate(-1));

        Assert.Equal("base cost must not be negative", exception.Message);
    }

    [Fact]
    public void PreservesOverflowPolicyOfTheExpeditedVariant()
    {
        var legacy = new LegacyDeploymentCostCalculator();
        var refactored = new DeploymentCostCalculator(new ExpeditedCostPolicy());

        Assert.Throws<OverflowException>(
            () => legacy.Calculate(long.MaxValue, DeploymentMode.Expedited));
        Assert.Throws<OverflowException>(() => refactored.Calculate(long.MaxValue));
    }

    // Java passes the lambda `ignored -> 0`; a C# lambda cannot implement an interface.
    private sealed class ZeroCostPolicy : IDeploymentCostPolicy
    {
        public long Calculate(long baseCostInCents) => 0;
    }
}
