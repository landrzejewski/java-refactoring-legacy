namespace Training.Workshop.M7.S11MiddleMan.Step1;

/// <summary>
/// Krok 1: plakietka sama decyduje, że nieznany seans wygląda jak wyprzedany
/// (zachowanie przeniesione z pośrednika).
/// </summary>
public sealed class SeatBadge
{
    private readonly CinemaFacade _cinema;

    public SeatBadge(CinemaFacade cinema)
    {
        ArgumentNullException.ThrowIfNull(cinema);
        _cinema = cinema;
    }

    public string Badge(string id)
    {
        var free = FreeSeats(id);
        if (free == 0)
        {
            return id + ": WYPRZEDANE";
        }
        return _cinema.Title(id) + " (" + _cinema.Format(id) + "): " + free + " wolnych";
    }

    private int FreeSeats(string id)
    {
        try
        {
            return _cinema.FreeSeats(id);
        }
        catch (KeyNotFoundException)
        {
            return 0;
        }
    }
}
