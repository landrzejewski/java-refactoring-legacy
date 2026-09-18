using System.Globalization;
using Training.Module8.Incremental;

namespace Training.Module8.Tests.Incremental;

public sealed class PricingEngineContractTest
{
    public static TheoryData<string, IPricingEngine> Engines() =>
        new()
        {
            {
                "legacy implementation behind an adapter",
                new LegacyPricingEngineAdapter()
            },
            {
                "candidate implementation",
                new CandidatePricingEngine()
            }
        };

    [Theory]
    [MemberData(nameof(Engines))]
    public void EveryProductionImplementationCalculatesCanonicalExamples(
        string description,
        IPricingEngine engine)
    {
        Assert.NotEmpty(description);
        Assert.Multiple(
            () => AssertQuote(engine, "10.00", 3, "0", "30.00"),
            () => AssertQuote(engine, "10.00", 3, "1", "0.00"),
            () => AssertQuote(engine, "0.01", 1, "0.5", "0.01"),
            () => AssertQuote(engine, "19.995", 2, "0.12555", "34.98"),
            () => AssertQuote(engine, "0.05", 3, "0.3333", "0.10"));
    }

    [Theory]
    [MemberData(nameof(Engines))]
    public void EveryProductionImplementationIsDeterministicAndReturnsMoney(
        string description,
        IPricingEngine engine)
    {
        Assert.NotEmpty(description);
        var request = new PriceRequest(17.49m, 7, 0.075m);

        PriceQuote first = engine.Quote(request);
        PriceQuote second = engine.Quote(request);

        Assert.Multiple(
            () => Assert.Equal(first, second),
            () => Assert.Equal(2, first.NetAmount.Scale),
            () => Assert.Equal(113.25m, first.NetAmount));
    }

    [Theory]
    [MemberData(nameof(Engines))]
    public void EveryProductionImplementationRejectsMissingRequest(
        string description,
        IPricingEngine engine)
    {
        Assert.NotEmpty(description);
        var failure = Assert.Throws<ArgumentNullException>(
            () => engine.Quote(null!));

        Assert.Equal("request", failure.ParamName);
    }

    private static void AssertQuote(
        IPricingEngine engine,
        string unitPrice,
        int quantity,
        string discountRate,
        string expected)
    {
        PriceQuote quote = engine.Quote(new PriceRequest(
            decimal.Parse(unitPrice, CultureInfo.InvariantCulture),
            quantity,
            decimal.Parse(discountRate, CultureInfo.InvariantCulture)));

        Assert.Equal(
            decimal.Parse(expected, CultureInfo.InvariantCulture),
            quote.NetAmount);
        Assert.Equal(2, quote.NetAmount.Scale);
    }
}
