namespace Training.Workshop.M3.S02Similarity.Step1;

/// <summary>
/// Krok 1: Inline Method - wspólna metoda wróciła do obu wywołujących.
/// Tymczasowe powtórzenie kodu to bezpieczny etap rozdzielania reguł.
/// </summary>
public sealed class OnlineCheckout
{
    public decimal Total(IReadOnlyList<decimal> ticketPrices)
    {
        var tickets = ticketPrices.Sum();
#pragma warning disable CS1718 // porównanie stałej ze sobą - ślad po Inline Method, usuwany w kroku 2
        var perUnit = ServiceFee.Kind.OnlineBooking == ServiceFee.Kind.OnlineBooking
            ? 2.00m : 3.00m;
#pragma warning restore CS1718
        return tickets + Math.Round(perUnit * ticketPrices.Count, 2, MidpointRounding.AwayFromZero);
    }
}
