namespace Training.Module3.Tests;

public sealed class DeliveryFeeLineCoverageTest
{
    [Fact]
    public void PremiumCustomerHasFreeDelivery()
    {
        Assert.Equal(0, DeliveryFee.Fee(true));
    }
}
