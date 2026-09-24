using System.Reflection;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12BridgeMethods.Step1;

/// <summary>
/// Krok 1: jedyna zmiana to <c>!IsBridge(type, method)</c>. Bez niej rejestr widział też Apply(ITicket)
/// i zgłaszał obsługę "ITicket" - test równoważności zrobił się czerwony. Tak wygląda pułapka refleksji
/// po wprowadzeniu hierarchii generycznej.
/// </summary>
public sealed class RuleRegistry
{
    private readonly Dictionary<Type, object> _rules = [];

    public RuleRegistry(params object[] rules)
    {
        foreach (var rule in rules)
        {
            var type = rule.GetType();
            foreach (var method in type.GetMethods(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.DeclaredOnly))
            {
                if (method.Name == "Apply" && !IsBridge(type, method))
                {
                    _rules[method.GetParameters()[0].ParameterType] = rule;
                }
            }
        }
    }

    public Money Price(ITicket ticket)
    {
        var rule = _rules.GetValueOrDefault(ticket.GetType());
        var method = rule!.GetType().GetMethod("Apply", [ticket.GetType()])
            ?? throw new InvalidOperationException("brak reguły dla " + ticket.GetType().Name);
        try
        {
            return (Money)method.Invoke(rule, [ticket])!;
        }
        catch (TargetInvocationException e)
        {
            throw new InvalidOperationException("brak reguły dla " + ticket.GetType().Name, e);
        }
    }

    public IReadOnlyList<string> SupportedTypes()
    {
        return _rules.Keys.Select(t => t.Name).Order(StringComparer.Ordinal).ToList();
    }

    public static RuleRegistry Standard()
    {
        return new RuleRegistry(new StandardRule(), new StudentRule());
    }

    /// <summary>
    /// "Most": metoda, która tylko implementuje nieogólny kontrakt IPriceRule (Apply(ITicket) z rzutowaniem).
    /// Odpowiednik Method.isBridge() z Javy - w .NET trzeba go wyliczyć z mapy interfejsu.
    /// </summary>
    private static bool IsBridge(Type type, MethodInfo method)
    {
        return typeof(IPriceRule).IsAssignableFrom(type)
            && type.GetInterfaceMap(typeof(IPriceRule)).TargetMethods.Contains(method);
    }
}
