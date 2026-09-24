namespace Training.Workshop.M5.S05ExtractSubclass.Step3;

/// <summary>Krok 3: bez zmian.</summary>
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
