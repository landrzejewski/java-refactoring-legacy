using Training.Workshop.Shared;

namespace Training.Workshop.M6.S20DecisionMap.Step1;

/// <summary>
/// Krok 1 (wspólny dla obu ścieżek): rozplecenie osi. Tabela 3x3 to w rzeczywistości dwie
/// niezależne reguły: cena bazowa formatu i korekta dnia. Extract Method dla każdej osi.
/// </summary>
public sealed class ShowPricing
{
    public Money Price(DayOfWeek day, string format)
    {
        return AdjustForDay(day, BasePrice(format));
    }

    private static Money BasePrice(string format) => format switch
    {
        "2D" => Money.Of("25.00"),
        "3D" => Money.Of("32.00"),
        "IMAX" => Money.Of("40.00"),
        _ => throw new ArgumentException("unknown format: " + format),
    };

    private static Money AdjustForDay(DayOfWeek day, Money basePrice) => day switch
    {
        DayOfWeek.Tuesday => basePrice.Minus(basePrice.Percent(30)),
        DayOfWeek.Saturday or DayOfWeek.Sunday => basePrice.Plus(Money.Of("2.00")),
        _ => basePrice,
    };
}
