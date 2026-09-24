namespace Training.Workshop.M3.S06Yagni.Step2;

/// <summary>Seans poranny -5.00.</summary>
public sealed class MorningRule : IPricingRule
{
    public bool AppliesTo(TicketQuote quote)
    {
        return quote.Start.Hour < 12;
    }

    public decimal Apply(decimal price)
    {
        return price - 5.00m;
    }
}
