namespace Training.Workshop.M3.S02Similarity.Start;

/// <summary>Sprzedaż online: suma biletów plus opłata rezerwacyjna za każdy bilet.</summary>
public sealed class OnlineCheckout
{
    public decimal Total(IReadOnlyList<decimal> ticketPrices)
    {
        var tickets = ticketPrices.Sum();
        return tickets + ServiceFee.Of(ServiceFee.Kind.OnlineBooking, ticketPrices.Count);
    }
}
