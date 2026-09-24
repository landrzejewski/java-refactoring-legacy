namespace Training.Workshop.M4.S11EncapsulateCollection.Start;

/// <summary>
/// Start: publiczna, mutowalna lista miejsc. <c>readonly</c> blokuje tylko przypisanie -
/// każdy może dodać, usunąć albo wyczyścić miejsca z pominięciem właściciela.
/// </summary>
public sealed class Booking
{
    private const decimal Price2D = 25.00m;
    private const decimal VipSurcharge = 10.00m;

    public readonly List<Seat> Seats = [];

    public decimal Total()
    {
        decimal total = 0.00m;
        foreach (var seat in Seats)
        {
            total = total + (seat.Row >= 10 ? Price2D + VipSurcharge : Price2D);
        }
        return total;
    }
}
