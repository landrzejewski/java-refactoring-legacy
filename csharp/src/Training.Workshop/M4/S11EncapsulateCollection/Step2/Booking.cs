namespace Training.Workshop.M4.S11EncapsulateCollection.Step2;

/// <summary>
/// Krok 2 (zmiana kontraktu): właściwość zwraca niemodyfikowalny WIDOK (<c>AsReadOnly()</c>).
/// Klient nie zmieni członkostwa (NotSupportedException), ale widzi późniejsze zmiany
/// właściciela. Bezpieczne dopiero, gdy żaden klient nie modyfikuje listy przez właściwość.
/// </summary>
public sealed class Booking
{
    private const decimal Price2D = 25.00m;
    private const decimal VipSurcharge = 10.00m;

    private readonly List<Seat> _seats = [];

    /// <summary>Żywy widok tylko do odczytu.</summary>
    public IReadOnlyList<Seat> Seats => _seats.AsReadOnly();

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
