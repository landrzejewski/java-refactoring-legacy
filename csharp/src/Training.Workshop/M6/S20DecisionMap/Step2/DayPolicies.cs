using Training.Workshop.Shared;

namespace Training.Workshop.M6.S20DecisionMap.Step2;

/// <summary>
/// Krok 2 (ścieżka A): kalendarz polityk. Gdy marketing co miesiąc dodaje akcje ("środa
/// seniora", "noc kina"), zmienia się tylko ten kalendarz albo powstaje nowy.
/// </summary>
public static class DayPolicies
{
    public static readonly DayPolicy CheapTuesday = basePrice => basePrice.Minus(basePrice.Percent(30));
    public static readonly DayPolicy Weekend = basePrice => basePrice.Plus(Money.Of("2.00"));
    public static readonly DayPolicy Regular = basePrice => basePrice;

    public static DayPolicy Standard(DayOfWeek day) => day switch
    {
        DayOfWeek.Tuesday => CheapTuesday,
        DayOfWeek.Saturday or DayOfWeek.Sunday => Weekend,
        _ => Regular,
    };
}
