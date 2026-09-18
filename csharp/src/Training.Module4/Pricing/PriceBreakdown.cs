namespace Training.Module4.Pricing;

public sealed record PriceBreakdown
{
    public PriceBreakdown(
        decimal baseRentalCost,
        decimal discount,
        decimal insuranceCost,
        decimal deliveryCost,
        decimal netAmount,
        decimal vat,
        decimal total)
    {
        BaseRentalCost = Money(baseRentalCost, nameof(baseRentalCost));
        Discount = Money(discount, nameof(discount));
        InsuranceCost = Money(insuranceCost, nameof(insuranceCost));
        DeliveryCost = Money(deliveryCost, nameof(deliveryCost));
        NetAmount = Money(netAmount, nameof(netAmount));
        Vat = Money(vat, nameof(vat));
        Total = Money(total, nameof(total));
    }

    public decimal BaseRentalCost { get; }

    public decimal Discount { get; }

    public decimal InsuranceCost { get; }

    public decimal DeliveryCost { get; }

    public decimal NetAmount { get; }

    public decimal Vat { get; }

    public decimal Total { get; }

    private static decimal Money(decimal amount, string name)
    {
        if (amount < 0m)
        {
            throw new ArgumentException($"{name} must not be negative", name);
        }
        // Equivalent of BigDecimal.setScale(2, RoundingMode.UNNECESSARY):
        // an amount with more than two significant decimal places is a bug.
        if (decimal.Round(amount, 2) != amount)
        {
            throw new ArithmeticException($"{name} must have at most two decimal places");
        }
        return amount;
    }
}
