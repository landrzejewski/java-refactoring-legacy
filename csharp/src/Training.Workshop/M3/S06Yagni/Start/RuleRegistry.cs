namespace Training.Workshop.M3.S06Yagni.Start;

/// <summary>Start: rejestr pluginów konfigurowany napisem "morning,vip" - "na przyszłość".</summary>
public sealed class RuleRegistry
{
    private readonly Dictionary<string, Func<IPricingRule>> _plugins = [];

    public static RuleRegistry WithDefaults()
    {
        var registry = new RuleRegistry();
        registry.Register("morning", () => new MorningRule());
        registry.Register("vip", () => new VipRule());
        return registry;
    }

    public void Register(string name, Func<IPricingRule> plugin)
    {
        _plugins[name] = plugin;
    }

    public IReadOnlyList<IPricingRule> Resolve(string activeRules)
    {
        return activeRules.Split(',')
            .Select(name => name.Trim())
            .Select(Create)
            .OrderBy(rule => rule.Priority)
            .ToList();
    }

    private IPricingRule Create(string name)
    {
        if (!_plugins.TryGetValue(name, out var plugin))
        {
            throw new ArgumentException("brak reguly: " + name);
        }
        return plugin();
    }
}
