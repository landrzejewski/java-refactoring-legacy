namespace Training.Workshop.M3.S06Yagni.Step1;

/// <summary>Miejsce VIP +10.00.</summary>
public sealed class VipRule : IPricingRule
{
    public bool AppliesTo(IDictionary<string, object> context)
    {
        return (int)context["row"] >= (int)context["vipFromRow"];
    }

    public decimal Apply(IDictionary<string, object> context, decimal price)
    {
        return price + 10.00m;
    }
}
