using Training.Workshop.Shared;

namespace Training.Workshop.M5.S05ExtractSubclass.Step1;

/// <summary>
/// Krok 1: Replace Constructor with Factory Method - Regular(...) i Premiere(...).
/// Punkty tworzenia są teraz w jednym miejscu; to one za chwilę wybiorą klasę runtime.
/// </summary>
public sealed class Screening
{
    private readonly string _title;
    private readonly string _format;
    private readonly bool _premiere;
    private readonly string? _guest;

    private Screening(string title, string format, bool premiere, string? guest)
    {
        _title = title;
        _format = format;
        _premiere = premiere;
        _guest = guest;
    }

    public static Screening Regular(string title, string format)
    {
        return new Screening(title, format, false, null);
    }

    public static Screening Premiere(string title, string format, string guest)
    {
        return new Screening(title, format, true, guest);
    }

    public Money Price()
    {
        var @base = _format switch
        {
            "IMAX" => Money.Of("40.00"),
            "3D" => Money.Of("32.00"),
            _ => Money.Of("25.00"),
        };
        return _premiere ? @base.Plus(Money.Of("15.00")) : @base;
    }

    public string Describe()
    {
        var text = _title + " (" + _format + ")";
        if (_premiere)
        {
            text += " - premiera, gość: " + _guest;
        }
        return text;
    }
}
