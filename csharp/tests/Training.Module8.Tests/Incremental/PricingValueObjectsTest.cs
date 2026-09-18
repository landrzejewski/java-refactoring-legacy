using Training.Module8.Incremental;

namespace Training.Module8.Tests.Incremental;

public sealed class PricingValueObjectsTest
{
    [Fact]
    public void RequestNormalizesMoneyAndRateUsingTheDeclaredPolicy()
    {
        var request = new PriceRequest(12.345m, 2, 0.12345m);

        Assert.Multiple(
            () => Assert.Equal(12.34m, request.UnitPrice),
            () => Assert.Equal(0.1234m, request.DiscountRate),
            () => Assert.Equal(2, request.UnitPrice.Scale),
            () => Assert.Equal(4, request.DiscountRate.Scale));
    }

    [Fact]
    public void RequestRejectsInvalidValuesBeforeRounding()
    {
        // decimal jest typem wartościowym, więc przypadki null z Javy
        // (unitPrice, discountRate) nie mają odpowiednika w C#.
        AssertFailure(
            "unitPrice must not be negative",
            () => new PriceRequest(-0.001m, 1, 0m));
        AssertFailure(
            "quantity must be positive",
            () => new PriceRequest(1m, 0, 0m));
        AssertFailure(
            "discountRate must be between 0 and 1",
            () => new PriceRequest(1m, 1, -0.00001m));
        AssertFailure(
            "discountRate must be between 0 and 1",
            () => new PriceRequest(1m, 1, 1.00001m));
    }

    [Fact]
    public void QuoteNormalizesMoneyAndRejectsInvalidAmounts()
    {
        decimal netAmount = new PriceQuote(10.125m).NetAmount;
        Assert.Equal(10.12m, netAmount);
        Assert.Equal(2, netAmount.Scale);
        AssertFailure(
            "netAmount must not be negative",
            () => new PriceQuote(-0.001m));
    }

    private static void AssertFailure(string expectedMessage, Action action)
    {
        var failure = Assert.Throws<ArgumentException>(action);
        Assert.Equal(expectedMessage, failure.Message);
    }
}
