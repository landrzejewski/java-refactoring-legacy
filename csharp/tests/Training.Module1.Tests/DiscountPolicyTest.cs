namespace Training.Module1.Tests;

public sealed class DiscountPolicyTest
{
    [Fact]
    public void CombinesThresholdAndVipDiscount()
    {
        Assert.Equal(15, DiscountPolicy.DiscountPercent(100, true));
    }
}
