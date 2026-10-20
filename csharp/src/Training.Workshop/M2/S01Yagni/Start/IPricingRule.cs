namespace Training.Workshop.M2.S01Yagni.Start;

/// <summary>
/// Start: "rozszerzalny" kontrakt reguły cenowej - priorytet, generyczny kontekst,
/// dowolne pluginy. Istnieją dokładnie dwie implementacje i nikt nie zgłosił trzeciej.
/// </summary>
public interface IPricingRule
{
    int Priority { get; }

    bool AppliesTo(IDictionary<string, object> context);

    decimal Apply(IDictionary<string, object> context, decimal price);
}
