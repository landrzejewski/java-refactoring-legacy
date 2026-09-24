using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12BridgeMethods.Step2;

/// <summary>Krok 2: rejestr bez refleksji - typ z kontraktu, rzutowanie w jednym miejscu (kontrakt IPriceRule&lt;T&gt;).</summary>
public sealed class RuleRegistry
{
    private readonly Dictionary<Type, IPriceRule> _rules = [];

    public RuleRegistry(params IPriceRule[] rules)
    {
        foreach (var rule in rules)
        {
            _rules[rule.TicketType] = rule;
        }
    }

    public Money Price(ITicket ticket)
    {
        if (!_rules.TryGetValue(ticket.GetType(), out var rule))
        {
            throw new InvalidOperationException("brak reguły dla " + ticket.GetType().Name);
        }
        return rule.Apply(ticket);
    }

    public IReadOnlyList<string> SupportedTypes()
    {
        return _rules.Keys.Select(t => t.Name).Order(StringComparer.Ordinal).ToList();
    }

    public static RuleRegistry Standard()
    {
        return new RuleRegistry(new StandardRule(), new StudentRule());
    }
}
