using Training.Workshop.Shared;

namespace Training.Workshop.M6.S20DecisionMap.Step3;

/// <summary>
/// Krok 3 (alternatywa dla kroku 2, budowana od kroku 1): Replace Type Code with Class -
/// wiedza o cenie należy do formatu. Reguła dnia zostaje zwykłym switchem, bo jest stabilna.
/// </summary>
public sealed class ShowPricing
{
    public Money Price(DayOfWeek day, string format)
    {
        return Format.Of(format).PriceOn(day);
    }
}
