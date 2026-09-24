using Training.Workshop.Shared;

namespace Training.Workshop.M6.S20DecisionMap.Start;

/// <summary>
/// Start: tabela cen wpisana w zagnieżdżone warunki - format x dzień tygodnia. Dwie osie
/// zmienności (format i reguła dnia: "tani wtorek" -30%, weekend +2.00) są splecione.
/// </summary>
public sealed class ShowPricing
{
    public Money Price(DayOfWeek day, string format)
    {
        var weekend = day == DayOfWeek.Saturday || day == DayOfWeek.Sunday;
        if (format == "2D")
        {
            return Money.Of(day == DayOfWeek.Tuesday ? "17.50" : weekend ? "27.00" : "25.00");
        }
        else if (format == "3D")
        {
            return Money.Of(day == DayOfWeek.Tuesday ? "22.40" : weekend ? "34.00" : "32.00");
        }
        else if (format == "IMAX")
        {
            return Money.Of(day == DayOfWeek.Tuesday ? "28.00" : weekend ? "42.00" : "40.00");
        }
        throw new ArgumentException("unknown format: " + format);
    }
}
