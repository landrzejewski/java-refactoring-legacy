namespace Training.Module8.Incremental;

public sealed record PriceRequest
{
    private const int MoneyScale = 2;
    private const int RateScale = 4;
    private const decimal MaximumDiscountRate = 1m;

    public PriceRequest(
        decimal unitPrice,
        int quantity,
        decimal discountRate)
    {
        if (unitPrice < 0)
        {
            throw new ArgumentException(
                "unitPrice must not be negative");
        }
        if (quantity <= 0)
        {
            throw new ArgumentException("quantity must be positive");
        }
        if (discountRate < 0 || discountRate > MaximumDiscountRate)
        {
            throw new ArgumentException(
                "discountRate must be between 0 and 1");
        }

        UnitPrice = Money.SetScale(unitPrice, MoneyScale);
        Quantity = quantity;
        DiscountRate = Money.SetScale(discountRate, RateScale);
    }

    public decimal UnitPrice { get; }

    public int Quantity { get; }

    public decimal DiscountRate { get; }
}
