namespace Training.Module8.Incremental;

public sealed class LegacyPriceCalculator
{
    private const int MoneyScale = 2;
    private const int PercentScale = 2;
    private const decimal MaximumDiscountPercent = 100m;

    public decimal Calculate(
        decimal unitPrice,
        int quantity,
        decimal discountPercent)
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
        if (discountPercent < 0
            || discountPercent > MaximumDiscountPercent)
        {
            throw new ArgumentException(
                "discountPercent must be between 0 and 100");
        }

        decimal normalizedUnitPrice = Money.SetScale(unitPrice, MoneyScale);
        decimal normalizedDiscountPercent = Money.SetScale(
            discountPercent, PercentScale);
        decimal grossAmount = normalizedUnitPrice * quantity;
        decimal discountAmount = Money.SetScale(
            grossAmount * normalizedDiscountPercent / 100m,
            MoneyScale);

        return Money.SetScale(grossAmount - discountAmount, MoneyScale);
    }
}
