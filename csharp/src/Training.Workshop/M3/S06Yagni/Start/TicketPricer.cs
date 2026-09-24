namespace Training.Workshop.M3.S06Yagni.Start;

/// <summary>
/// Start: spekulatywny silnik reguł cenowych (YAGNI). Rejestr pluginów, konfiguracja
/// napisem, priorytety i kontekst IDictionary&lt;string, object&gt; - wszystko dla DWÓCH reguł,
/// które nie zależą od kolejności. Literówka w konfiguracji wybucha dopiero w runtime.
/// </summary>
public sealed class TicketPricer
{
    private readonly RuleRegistry _registry;
    private readonly string _activeRules;

    public TicketPricer()
        : this(RuleRegistry.WithDefaults(), "morning,vip")
    {
    }

    public TicketPricer(RuleRegistry registry, string activeRules)
    {
        _registry = registry;
        _activeRules = activeRules;
    }

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
        foreach (var rule in _registry.Resolve(_activeRules))
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
