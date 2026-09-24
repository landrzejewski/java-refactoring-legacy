using System.Globalization;

namespace Training.Workshop.M3.S15Invariants.Step2;

/// <summary>Krok 2: serwis nie powtarza reguł modelu - tylko koordynuje.</summary>
public sealed class BookingService
{
    public string Book(string email, int seats, decimal total)
    {
        var reservation = new Reservation(email, seats, total);
        return "zarezerwowano: " + reservation.Email + ", miejsc " + reservation.Seats
            + ", kwota " + reservation.Total.ToString(CultureInfo.InvariantCulture);
    }
}
