namespace Training.Workshop.M3.S06Yagni.Step1;

/// <summary>
/// Krok 1: Inline Class - rejestr pluginów i konfiguracja napisem znikają.
/// Reguły to zwykła lista tworzona przez new; priorytet (Safe Delete) był potrzebny
/// tylko do sortowania w rejestrze. Literówka w nazwie reguły już się nie skompiluje.
/// </summary>
public sealed class TicketPricer
{
    private readonly IReadOnlyList<IPricingRule> _rules = [new MorningRule(), new VipRule()];

    public decimal Price(TicketQuote quote)
    {
        var context = new Dictionary<string, object>
        {
            ["format"] = quote.Format,
            ["start"] = quote.Start,
            ["row"] = quote.Row,
            ["vipFromRow"] = quote.VipFromRow,
        };
        var price = BasePrice(quote.Format);
        foreach (var rule in _rules)
        {
            if (rule.AppliesTo(context))
            {
                price = rule.Apply(context, price);
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
