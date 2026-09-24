namespace Training.Workshop.M7.S04RemoveDuplication.Step1;

/// <summary>
/// Krok 1: ujednolicenie zapisu - stałe GroupSize i GroupDiscount, warunek &gt;= zamiast &gt; 9,
/// zmienna tickets. Zachowanie bez zmian; różnica względem WebShop to już tylko tryb zaokrąglenia.
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
