namespace Training.Workshop.M5.S22MiddleMan.Start;

/// <summary>Klient 2: tablica seansów w holu. Korzysta z pośrednika.</summary>
public sealed class DailyBoard
{
    private readonly CinemaFacade _cinema;

    public DailyBoard(CinemaFacade cinema)
    {
        ArgumentNullException.ThrowIfNull(cinema);
        _cinema = cinema;
    }

    public string Render()
    {
        return string.Join("\n", _cinema.Screenings()
            .Select(screening => screening.Id + " " + screening.Title + " " + screening.Format));
    }
}
