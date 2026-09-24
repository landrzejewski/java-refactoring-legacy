using System.Globalization;

namespace Training.Workshop.M3.S15Invariants.Step1;

/// <summary>Krok 1: walidacja nadal tutaj, tworzenie przez konstruktor rekordu.</summary>
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
        var reservation = new Reservation(email, seats, total);
        return "zarezerwowano: " + reservation.Email + ", miejsc " + reservation.Seats
            + ", kwota " + reservation.Total.ToString(CultureInfo.InvariantCulture);
    }
}
