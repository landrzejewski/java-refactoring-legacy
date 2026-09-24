namespace Training.Workshop.M7.S11MiddleMan.Step1;

/// <summary>Klient 2: bez zmian w kroku 1 - nadal korzysta z pośrednika.</summary>
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
