namespace Training.Workshop.M3.S02Similarity.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): Extract Constant z nazwą w języku domeny, u właściciela reguły.
/// Opłata rezerwacyjna należy do sprzedaży online i zmienia się z jej powodów
/// (promocje, konkurencja). Nie ma nic wspólnego z potrąceniem przy zwrocie.
/// </summary>
public sealed class OnlineCheckout
{
    private const decimal BookingFeePerTicket = 2.00m;

    public decimal Total(IReadOnlyList<decimal> ticketPrices)
    {
        var tickets = ticketPrices.Sum();
        return tickets + BookingFeePerTicket * ticketPrices.Count;
    }
}
