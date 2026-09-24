namespace Training.Workshop.M7.S04RemoveDuplication.Step1;

/// <summary>
/// Krok 1: ujednolicenie zapisu - ten sam kształt, te same nazwy i stałe co w BoxOffice.
/// Zachowanie bez zmian: jedyna różnica (ToEven, czyli HALF_EVEN) stoi teraz w jednej, widocznej linii.
/// </summary>
public sealed class WebShop
{
    private const int GroupSize = 10;
    private const decimal GroupDiscount = 0.10m;
    private const decimal Fee = 2.00m;

    public decimal Total(IReadOnlyList<decimal> ticketPrices)
    {
        var tickets = 0m;
        foreach (var price in ticketPrices)
        {
            tickets += price;
        }
        if (ticketPrices.Count >= GroupSize)
        {
            var discount = Math.Round(tickets * GroupDiscount, 2, MidpointRounding.ToEven);
            tickets -= discount;
        }
        var fees = Fee * ticketPrices.Count;
        return Math.Round(tickets + fees, 2, MidpointRounding.AwayFromZero);
    }
}
