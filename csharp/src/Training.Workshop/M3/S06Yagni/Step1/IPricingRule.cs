namespace Training.Workshop.M3.S06Yagni.Step1;

/// <summary>Krok 1: bez priorytetu - kolejność wyznacza lista w TicketPricer.</summary>
public interface IPricingRule
{
    bool AppliesTo(IDictionary<string, object> context);

    decimal Apply(IDictionary<string, object> context, decimal price);
}
