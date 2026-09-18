namespace Training.Module8.Incremental;

public sealed record PriceQuote
{
    private const int MoneyScale = 2;

    public PriceQuote(decimal netAmount)
    {
        if (netAmount < 0)
        {
            throw new ArgumentException(
                "netAmount must not be negative");
        }
        NetAmount = Money.SetScale(netAmount, MoneyScale);
    }

    public decimal NetAmount { get; }
}
