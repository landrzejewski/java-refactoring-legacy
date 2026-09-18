namespace Training.Module8.Incremental;

public sealed class LegacyPricingEngineAdapter : IPricingEngine
{
    private readonly LegacyPriceCalculator calculator;

    public LegacyPricingEngineAdapter()
        : this(new LegacyPriceCalculator())
    {
    }

    public LegacyPricingEngineAdapter(LegacyPriceCalculator calculator)
    {
        ArgumentNullException.ThrowIfNull(calculator);
        this.calculator = calculator;
    }

    public PriceQuote Quote(PriceRequest request)
    {
        ArgumentNullException.ThrowIfNull(request);
        return new PriceQuote(calculator.Calculate(
            request.UnitPrice,
            request.Quantity,
            request.DiscountRate * 100m));
    }
}
