namespace Training.Workshop.M3.S06Yagni.Step2;

/// <summary>Miejsce VIP +10.00.</summary>
public sealed class VipRule : IPricingRule
{
    public bool AppliesTo(TicketQuote quote)
    {
        return quote.Row >= quote.VipFromRow;
    }

    public decimal Apply(decimal price)
    {
        return price + 10.00m;
    }
}
