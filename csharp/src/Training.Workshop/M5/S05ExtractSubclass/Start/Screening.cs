using Training.Workshop.Shared;

namespace Training.Workshop.M5.S05ExtractSubclass.Start;

/// <summary>
/// Start: flaga <c>_premiere</c> i pole <c>_guest</c>, które ma sens tylko dla premier.
/// Dla zwykłego seansu guest == null, a każda metoda powtarza "if (_premiere)".
/// </summary>
public sealed class Screening
{
    private readonly string _title;
    private readonly string _format;
    private readonly bool _premiere;
    private readonly string? _guest;

    public Screening(string title, string format, bool premiere, string? guest)
    {
        _title = title;
        _format = format;
        _premiere = premiere;
        _guest = guest;
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
