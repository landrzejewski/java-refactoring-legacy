using System.Globalization;

namespace Training.Workshop.M3.S15Invariants.Start;

/// <summary>Start: jedyne miejsce, które waliduje - bo akurat ktoś pamiętał.</summary>
public sealed class BookingService
{
    public string Book(string email, int seats, decimal total)
    {
        if (email is null || !email.Contains('@'))
        {
            throw new ArgumentException("niepoprawny email: " + email);
        }
        if (seats < 1)
        {
            throw new ArgumentException("liczba miejsc musi byc dodatnia: " + seats);
        }
        if (total < 0)
        {
            throw new ArgumentException("kwota nie moze byc ujemna: " + total.ToString(CultureInfo.InvariantCulture));
        }
        var reservation = new Reservation();
        reservation.Email = email;
        reservation.Seats = seats;
        reservation.Total = total;
        return "zarezerwowano: " + reservation.Email + ", miejsc " + reservation.Seats
            + ", kwota " + reservation.Total.ToString(CultureInfo.InvariantCulture);
    }
}
