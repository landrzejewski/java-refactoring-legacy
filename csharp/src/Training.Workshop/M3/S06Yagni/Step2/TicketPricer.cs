namespace Training.Workshop.M3.S06Yagni.Step2;

/// <summary>
/// Krok 2: Change Signature - reguły dostają <see cref="TicketQuote"/> zamiast generycznej
/// mapy "kontekstu". Znika budowanie mapy i rzutowania; kompilator pilnuje nazw pól.
/// </summary>
public sealed class TicketPricer
{
    private readonly IReadOnlyList<IPricingRule> _rules = [new MorningRule(), new VipRule()];

    public decimal Price(TicketQuote quote)
    {
        var price = BasePrice(quote.Format);
        foreach (var rule in _rules)
        {
            if (rule.AppliesTo(quote))
            {
                price = rule.Apply(price);
            }
        }
        return Math.Round(price, 2, MidpointRounding.AwayFromZero);
    }

    private static decimal BasePrice(string format)
    {
        return format switch
        {
            "IMAX" => 40.00m,
            "3D" => 32.00m,
            _ => 25.00m,
        };
    }
}
