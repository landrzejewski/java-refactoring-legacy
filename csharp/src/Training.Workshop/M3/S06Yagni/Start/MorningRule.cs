namespace Training.Workshop.M3.S06Yagni.Start;

/// <summary>Start: plugin "seans poranny -5.00".</summary>
public sealed class MorningRule : IPricingRule
{
    public int Priority => 10;

    public bool AppliesTo(IDictionary<string, object> context)
    {
        return ((TimeOnly)context["start"]).Hour < 12;
    }

    public decimal Apply(IDictionary<string, object> context, decimal price)
    {
        return price - 5.00m;
    }
}
