namespace Training.Workshop.M5.S05ExtractSubclass.Step1;

/// <summary>Krok 1: klient wybiera wariant przez nazwę fabryki, a nie przez flagę i null.</summary>
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
