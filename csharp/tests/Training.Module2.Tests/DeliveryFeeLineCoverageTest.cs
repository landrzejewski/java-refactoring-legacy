namespace Training.Module2.Tests;

public sealed class DeliveryFeeLineCoverageTest
{
    [Fact]
    public void PremiumCustomerHasFreeDelivery()
    {
        Assert.Equal(0, DeliveryFee.Fee(true));
    }
}
