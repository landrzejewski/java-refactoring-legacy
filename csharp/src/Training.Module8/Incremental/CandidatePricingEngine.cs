namespace Training.Module8.Incremental;

public sealed class CandidatePricingEngine : IPricingEngine
{
    private const int MoneyScale = 2;

    public PriceQuote Quote(PriceRequest request)
    {
        ArgumentNullException.ThrowIfNull(request);

        decimal grossAmount = request.UnitPrice * request.Quantity;
        decimal discountAmount = Money.SetScale(
            grossAmount * request.DiscountRate, MoneyScale);

        return new PriceQuote(grossAmount - discountAmount);
    }
}
