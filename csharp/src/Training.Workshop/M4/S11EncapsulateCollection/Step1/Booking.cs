namespace Training.Workshop.M4.S11EncapsulateCollection.Step1;

/// <summary>
/// Krok 1: Encapsulate Collection, faza przejściowa - pole prywatne, operacje AddSeat/RemoveSeat
/// w właścicielu, a właściwość zwraca TĘ SAMĄ listę. Zachowujemy stary alias, więc to nadal czysta
/// refaktoryzacja: klient, który jeszcze modyfikuje listę przez właściwość, działa jak wcześniej.
/// </summary>
public sealed class Booking
{
    private const decimal Price2D = 25.00m;
    private const decimal VipSurcharge = 10.00m;

    private readonly List<Seat> _seats = [];

    /// <summary>Przejściowo: żywa, MODYFIKOWALNA lista - dokładnie to, co dawało publiczne pole.</summary>
    public List<Seat> Seats => _seats;

    public void AddSeat(Seat seat)
    {
        _seats.Add(seat);
    }

    public void RemoveSeat(Seat seat)
    {
        _seats.Remove(seat);
    }

    public decimal Total()
    {
        decimal total = 0.00m;
        foreach (var seat in _seats)
        {
            total = total + (seat.Row >= 10 ? Price2D + VipSurcharge : Price2D);
        }
        return total;
    }
}
