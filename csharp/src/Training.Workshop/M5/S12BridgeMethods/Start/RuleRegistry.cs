using System.Reflection;
using Training.Workshop.Shared;

namespace Training.Workshop.M5.S12BridgeMethods.Start;

/// <summary>
/// Start: rejestr "frameworkowy" - dla każdej metody o nazwie Apply bierze typ parametru jako klucz.
/// Działa, dopóki w klasie reguły jest dokładnie jedna metoda Apply.
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
                if (method.Name == "Apply")
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
}
