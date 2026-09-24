namespace Training.Workshop.M7.S04RemoveDuplication.Step2;

/// <summary>
/// Krok 2: kasa bez zmian - zmienia się tylko WebShop (tryb zaokrąglenia rabatu).
/// </summary>
public sealed class BoxOffice
{
    private const int GroupSize = 10;
    private const decimal GroupDiscount = 0.10m;

    public decimal Total(IReadOnlyList<decimal> ticketPrices)
    {
        var tickets = 0m;
        foreach (var price in ticketPrices)
        {
            tickets += price;
        }
        if (ticketPrices.Count >= GroupSize)
        {
            var discount = Math.Round(tickets * GroupDiscount, 2, MidpointRounding.AwayFromZero);
            tickets -= discount;
        }
        return Math.Round(tickets, 2, MidpointRounding.AwayFromZero);
    }
}
