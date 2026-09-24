using Training.Workshop.Shared;

namespace Training.Workshop.M6.S02Polymorphism.Start;

/// <summary>
/// Start: ten sam switch po rodzaju seansu w trzech metodach, a pole _value znaczy raz
/// "minuty", raz "liczba filmów". Nowy rodzaj seansu = zmiana we wszystkich switchach.
/// </summary>
public sealed class Screening
{
    public enum Kind { Regular, Premiere, Marathon }

    private readonly Kind _kind;
    private readonly string _title;
    private readonly int _value;

    private Screening(Kind kind, string title, int value)
    {
        _kind = kind;
        _title = title;
        _value = value;
    }

    public static Screening FromRow(ScreeningRow row)
    {
        var kind = row.Kind switch
        {
            "REGULAR" => Kind.Regular,
            "PREMIERE" => Kind.Premiere,
            "MARATHON" => Kind.Marathon,
            _ => throw new ArgumentException("unknown screening kind: " + row.Kind),
        };
        return new Screening(kind, row.Title, row.Value);
    }

    public string Label()
    {
        return _kind switch
        {
            Kind.Regular => _title,
            Kind.Premiere => "Premiera: " + _title,
            Kind.Marathon => "Maraton: " + _title + " (" + _value + " filmy)",
            _ => throw new ArgumentOutOfRangeException(nameof(_kind)),
        };
    }

    public int DurationMinutes()
    {
        return _kind switch
        {
            Kind.Regular => 20 + _value,
            Kind.Premiere => 30 + _value,
            Kind.Marathon => _value * 120 + (_value - 1) * 15,
            _ => throw new ArgumentOutOfRangeException(nameof(_kind)),
        };
    }

    public Money Price()
    {
        return _kind switch
        {
            Kind.Regular => Money.Of("25.00"),
            Kind.Premiere => Money.Of("35.00"),
            Kind.Marathon => Money.Of("20.00").Times(_value),
            _ => throw new ArgumentOutOfRangeException(nameof(_kind)),
        };
    }
}
