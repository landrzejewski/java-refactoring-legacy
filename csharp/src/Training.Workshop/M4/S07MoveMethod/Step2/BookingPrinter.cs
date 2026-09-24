namespace Training.Workshop.M4.S07MoveMethod.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): Move Method <c>RemainingSeats</c>
/// -&gt; <c>Screening.FreeSeatsWithout(int?)</c>. BookingPrinter zostaje koordynatorem wydruku:
/// składa tekst z tego, co wiedzą Booking i Screening.
/// </summary>
public sealed class BookingPrinter
{
    public string Print(Booking booking)
    {
        return "Rezerwacja " + booking.Id + "\n"
            + booking.Screening.Headline() + "\n"
            + "Miejsce: " + booking.Seat + "\n"
            + "Pozostale wolne: " + booking.Screening.FreeSeatsWithout(booking.Seat) + "\n";
    }
}
