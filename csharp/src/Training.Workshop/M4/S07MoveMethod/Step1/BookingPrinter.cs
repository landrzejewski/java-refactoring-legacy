namespace Training.Workshop.M4.S07MoveMethod.Step1;

/// <summary>
/// Krok 1: Move Method <c>ScreeningLine</c> -&gt; <c>Screening.Headline()</c>.
/// Metoda używała tylko danych seansu, więc właścicielem jest Screening.
/// Wywołanie po przeniesieniu czyta się <c>booking.Screening.Headline()</c>.
/// </summary>
public sealed class BookingPrinter
{
    public string Print(Booking booking)
    {
        return "Rezerwacja " + booking.Id + "\n"
            + booking.Screening.Headline() + "\n"
            + "Miejsce: " + booking.Seat + "\n"
            + "Pozostale wolne: " + RemainingSeats(booking.Screening, booking.Seat) + "\n";
    }

    private SeatList RemainingSeats(Screening s, int? seat)
    {
        var free = new SeatList(s.FreeSeats);
        free.Remove(seat);
        return free;
    }
}
