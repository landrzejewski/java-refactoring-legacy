using System.Collections.Immutable;

namespace Training.Workshop.M4.S11EncapsulateCollection.Step3;

/// <summary>
/// Krok 3 (zmiana kontraktu): właściwość zwraca MIGAWKĘ (<c>ToImmutableList()</c>).
/// Klient nie zmieni członkostwa i NIE widzi późniejszych zmian - dostaje stan z chwili wywołania.
/// Wybór między widokiem a migawką to decyzja o kontrakcie, nie szczegół implementacji.
/// </summary>
public sealed class Booking
{
    private const decimal Price2D = 25.00m;
    private const decimal VipSurcharge = 10.00m;

    private readonly List<Seat> _seats = [];

    /// <summary>Niemodyfikowalna kopia z chwili wywołania.</summary>
    public IReadOnlyList<Seat> Seats => _seats.ToImmutableList();

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
