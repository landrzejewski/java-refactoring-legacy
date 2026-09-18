namespace Training.Module1.Tests;

public sealed class RiskClassifierTest
{
    [Fact]
    public void CountsIndependentRiskConditions()
    {
        OrderSummary order = new(
            1500.00m,
            true,
            [new Item(true), new Item(false)]);

        Assert.Equal(3, RiskClassifier.RiskLevel(order));
    }
}
