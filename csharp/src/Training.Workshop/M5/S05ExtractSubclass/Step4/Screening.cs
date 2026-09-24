using Training.Workshop.Shared;

namespace Training.Workshop.M5.S05ExtractSubclass.Step4;

/// <summary>
/// Krok 4 (rozwiązanie): flaga _premiere usunięta - wariant wyraża klasa runtime.
/// Baza zna tylko zwykły seans; dopłatę premierową dodaje override Price() w podklasie.
/// </summary>
public class Screening
{
    private readonly string _title;
    private readonly string _format;

    private protected Screening(string title, string format)
    {
        _title = title;
        _format = format;
    }

    public static Screening Regular(string title, string format)
    {
        return new Screening(title, format);
    }

    public static Screening Premiere(string title, string format, string guest)
    {
        return new PremiereScreening(title, format, guest);
    }

    public virtual Money Price()
    {
        return _format switch
        {
            "IMAX" => Money.Of("40.00"),
            "3D" => Money.Of("32.00"),
            _ => Money.Of("25.00"),
        };
    }

    public virtual string Describe()
    {
        return _title + " (" + _format + ")";
    }
}
