namespace Training.Workshop.M7.S11MiddleMan.Start;

/// <summary>Klient 1: plakietka z liczbą wolnych miejsc. Korzysta z pośrednika.</summary>
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
        var free = _cinema.FreeSeats(id);
        if (free == 0)
        {
            return id + ": WYPRZEDANE";
        }
        return _cinema.Title(id) + " (" + _cinema.Format(id) + "): " + free + " wolnych";
    }
}
