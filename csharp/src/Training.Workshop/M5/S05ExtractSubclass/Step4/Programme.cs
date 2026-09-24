namespace Training.Workshop.M5.S05ExtractSubclass.Step4;

/// <summary>Krok 4: bez zmian - klient przeżył całą ekstrakcję bez modyfikacji od kroku 1.</summary>
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
