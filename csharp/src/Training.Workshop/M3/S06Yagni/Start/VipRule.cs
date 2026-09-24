namespace Training.Workshop.M3.S06Yagni.Start;

/// <summary>Start: plugin "miejsce VIP +10.00".</summary>
public sealed class VipRule : IPricingRule
{
    public int Priority => 20;

    public bool AppliesTo(IDictionary<string, object> context)
    {
        return (int)context["row"] >= (int)context["vipFromRow"];
    }

    public decimal Apply(IDictionary<string, object> context, decimal price)
    {
        return price + 10.00m;
    }
}
