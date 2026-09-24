using Training.Workshop.Shared;

namespace Training.Workshop.M6.S20DecisionMap.Step3;

/// <summary>
/// Krok 3 (ścieżka B - zmienia się zestaw formatów): format jako typ z zachowaniem (odpowiednik
/// enuma z Javy: klasa z instancjami statycznymi). Nowy format (4DX, ScreenX) to jedna instancja;
/// wyjątek od reguły dnia - osobna reguła w PriceOn.
/// </summary>
public sealed class Format
{
    public static readonly Format TwoD = new("2D", "25.00");
    public static readonly Format ThreeD = new("3D", "32.00");
    public static readonly Format Imax = new("IMAX", "40.00");

    private static readonly IReadOnlyList<Format> Values = [TwoD, ThreeD, Imax];

    private readonly string _code;
    private readonly Money _basePrice;

    private Format(string code, string basePrice)
    {
        _code = code;
        _basePrice = Money.Of(basePrice);
    }

    public static Format Of(string code)
    {
        foreach (var format in Values)
        {
            if (format._code == code)
            {
                return format;
            }
        }
        throw new ArgumentException("unknown format: " + code);
    }

    public Money PriceOn(DayOfWeek day) => day switch
    {
        DayOfWeek.Tuesday => _basePrice.Minus(_basePrice.Percent(30)),
        DayOfWeek.Saturday or DayOfWeek.Sunday => _basePrice.Plus(Money.Of("2.00")),
        _ => _basePrice,
    };
}
