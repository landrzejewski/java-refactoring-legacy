namespace Training.Workshop.M5.S05ExtractSubclass.Start;

/// <summary>Start: klient importujący repertuar - guest == null oznacza zwykły seans.</summary>
public sealed class Programme
{
    public string Line(string title, string format, string? guest)
    {
        var screening = new Screening(title, format, guest != null, guest);
        return screening.Describe() + " | " + screening.Price();
    }
}
