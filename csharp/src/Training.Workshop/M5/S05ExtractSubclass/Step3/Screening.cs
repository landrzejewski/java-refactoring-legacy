using Training.Workshop.Shared;

namespace Training.Workshop.M5.S05ExtractSubclass.Step3;

/// <summary>Krok 3: pole _guest zniknęło z bazy (Push Down Field) - razem z gałęzią premierową Describe().</summary>
public class Screening
{
    private readonly string _title;
    private readonly string _format;
    private readonly bool _premiere;

    private protected Screening(string title, string format, bool premiere)
    {
        _title = title;
        _format = format;
        _premiere = premiere;
    }

    public static Screening Regular(string title, string format)
    {
        return new Screening(title, format, false);
    }

    public static Screening Premiere(string title, string format, string guest)
    {
        return new PremiereScreening(title, format, guest);
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

    public virtual string Describe()
    {
        return _title + " (" + _format + ")";
    }
}
