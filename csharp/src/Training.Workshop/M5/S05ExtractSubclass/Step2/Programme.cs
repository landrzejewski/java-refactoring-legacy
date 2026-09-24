namespace Training.Workshop.M5.S05ExtractSubclass.Step2;

/// <summary>Krok 2: bez zmian - klient nie wie, że fabryka zwraca podklasę.</summary>
public sealed class Programme
{
    public string Line(string title, string format, string? guest)
    {
        var screening = guest == null
            ? Screening.Regular(title, format)
            : Screening.Premiere(title, format, guest);
        return screening.Describe() + " | " + screening.Price();
    }
}
