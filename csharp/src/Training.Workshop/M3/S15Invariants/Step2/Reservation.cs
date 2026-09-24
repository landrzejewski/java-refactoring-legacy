using System.Globalization;

namespace Training.Workshop.M3.S15Invariants.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): Move Method - strażnicy przeniesieni do konstruktora rekordu.
/// Inwarianty mają jednego właściciela: nie da się utworzyć rezerwacji w złym stanie,
/// niezależnie od ścieżki (kasa, online, import). C# nie ma kompaktowego konstruktora,
/// więc rekord ma jawny konstruktor i właściwości tylko do odczytu (bez init) - także
/// wyrażenie <c>with</c> nie ominie strażników.
/// </summary>
public sealed record Reservation
{
    public Reservation(string email, int seats, decimal total)
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
        Email = email;
        Seats = seats;
        Total = total;
    }

    public string Email { get; }

    public int Seats { get; }

    public decimal Total { get; }
}
