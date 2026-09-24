namespace Training.Workshop.M7.S04RemoveDuplication.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): sklep korzysta ze wspólnej reguły GroupDiscount; różnica
/// względem kasy (opłata 2.00 za bilet) zostaje jawna, w tej klasie.
/// </summary>
public sealed class WebShop
{
    private const decimal Fee = 2.00m;

    public decimal Total(IReadOnlyList<decimal> ticketPrices)
    {
        var fees = Fee * ticketPrices.Count;
        return Math.Round(GroupDiscount.TicketsTotal(ticketPrices) + fees, 2, MidpointRounding.AwayFromZero);
    }
}
