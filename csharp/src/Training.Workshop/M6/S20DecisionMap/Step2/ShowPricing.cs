using Training.Workshop.Shared;

namespace Training.Workshop.M6.S20DecisionMap.Step2;

/// <summary>
/// Krok 2 (ścieżka A - zmienia się reguła dnia): Replace Conditional Logic with Strategy.
/// Kalendarz polityk jest wstrzykiwany; format pozostaje prostym switchem, bo jest stabilny.
/// </summary>
public sealed class ShowPricing
{
    private readonly Func<DayOfWeek, DayPolicy> _calendar;

    public ShowPricing()
        : this(DayPolicies.Standard)
    {
    }

    public ShowPricing(Func<DayOfWeek, DayPolicy> calendar)
    {
        ArgumentNullException.ThrowIfNull(calendar);
        _calendar = calendar;
    }

    public Money Price(DayOfWeek day, string format)
    {
        return _calendar(day)(BasePrice(format));
    }

    private static Money BasePrice(string format) => format switch
    {
        "2D" => Money.Of("25.00"),
        "3D" => Money.Of("32.00"),
        "IMAX" => Money.Of("40.00"),
        _ => throw new ArgumentException("unknown format: " + format),
    };
}
