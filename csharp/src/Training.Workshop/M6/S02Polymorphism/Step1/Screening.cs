using Training.Workshop.Shared;

namespace Training.Workshop.M6.S02Polymorphism.Step1;

/// <summary>
/// Krok 1: Extract Subclass dla jednej gałęzi (Marathon). Tworzenie w FromRow kieruje maraton
/// do podklasy, a gałęzie Marathon w switchach stały się martwe - rzucają wyjątek.
/// Konstruktor <c>private protected</c> zamyka hierarchię (odpowiednik <c>sealed ... permits</c> z Javy).
/// </summary>
public class Screening
{
    public enum Kind { Regular, Premiere, Marathon }

    private readonly Kind _kind;
    private readonly string _title;
    private readonly int _value;

    private protected Screening(Kind kind, string title, int value)
    {
        _kind = kind;
        _title = title;
        _value = value;
    }

    public static Screening FromRow(ScreeningRow row)
    {
        return row.Kind switch
        {
            "REGULAR" => new Screening(Kind.Regular, row.Title, row.Value),
            "PREMIERE" => new Screening(Kind.Premiere, row.Title, row.Value),
            "MARATHON" => new MarathonScreening(row.Title, row.Value),
            _ => throw new ArgumentException("unknown screening kind: " + row.Kind),
        };
    }

    protected string Title => _title;

    public virtual string Label()
    {
        return _kind switch
        {
            Kind.Regular => _title,
            Kind.Premiere => "Premiera: " + _title,
            Kind.Marathon => throw new InvalidOperationException("handled by MarathonScreening"),
            _ => throw new ArgumentOutOfRangeException(nameof(_kind)),
        };
    }

    public virtual int DurationMinutes()
    {
        return _kind switch
        {
            Kind.Regular => 20 + _value,
            Kind.Premiere => 30 + _value,
            Kind.Marathon => throw new InvalidOperationException("handled by MarathonScreening"),
            _ => throw new ArgumentOutOfRangeException(nameof(_kind)),
        };
    }

    public virtual Money Price()
    {
        return _kind switch
        {
            Kind.Regular => Money.Of("25.00"),
            Kind.Premiere => Money.Of("35.00"),
            Kind.Marathon => throw new InvalidOperationException("handled by MarathonScreening"),
            _ => throw new ArgumentOutOfRangeException(nameof(_kind)),
        };
    }
}
