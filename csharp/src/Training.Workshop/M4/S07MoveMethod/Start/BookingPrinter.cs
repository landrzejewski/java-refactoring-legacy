using System.Globalization;

namespace Training.Workshop.M4.S07MoveMethod.Start;

/// <summary>
/// Start: wydruk rezerwacji. ScreeningLine i RemainingSeats używają WYŁĄCZNIE danych Screening
/// (Feature Envy). Print koordynuje Booking i Screening - ona zostaje tutaj.
/// </summary>
public sealed class BookingPrinter
{
    public string Print(Booking booking)
    {
        return "Rezerwacja " + booking.Id + "\n"
            + ScreeningLine(booking.Screening) + "\n"
            + "Miejsce: " + booking.Seat + "\n"
            + "Pozostale wolne: " + RemainingSeats(booking.Screening, booking.Seat) + "\n";
    }

    private string ScreeningLine(Screening s)
    {
        string format = s.Format switch
        {
            3 => "IMAX",
            2 => "3D",
            _ => "2D",
        };
        return s.Title + " (" + format + "), sala " + s.Hall + ", "
            + s.Start.ToString("yyyy-MM-dd HH:mm", CultureInfo.InvariantCulture);
    }

    private SeatList RemainingSeats(Screening s, int? seat)
    {
        var free = new SeatList(s.FreeSeats);
        free.Remove(seat);
        return free;
    }
}
