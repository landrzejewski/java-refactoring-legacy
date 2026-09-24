namespace Training.Workshop.M3.S02Similarity.Step2;

/// <summary>
/// Krok 2: Simplify - warunek na stałej zawsze prawdziwy, martwa gałąź usunięta.
/// ServiceFee usunięty (Safe Delete), bo nikt go już nie używa.
/// </summary>
public sealed class OnlineCheckout
{
    public decimal Total(IReadOnlyList<decimal> ticketPrices)
    {
        var tickets = ticketPrices.Sum();
        return tickets + 2.00m * ticketPrices.Count;
    }
}
