namespace Training.Workshop.M7.S11MiddleMan.Step2;

/// <summary>Klient 2: bez zmian w kroku 2 - nadal korzysta z pośrednika (migrujemy po jednym kliencie).</summary>
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
