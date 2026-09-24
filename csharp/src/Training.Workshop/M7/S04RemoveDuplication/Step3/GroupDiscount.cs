namespace Training.Workshop.M7.S04RemoveDuplication.Step3;

/// <summary>
/// Krok 3: Extract Class - reguła "10+ biletów = -10%, rabat zaokrąglany AwayFromZero (HALF_UP)" ma
/// jednego właściciela. Opłata rezerwacyjna nie jest częścią reguły i zostaje w WebShop.
/// </summary>
internal static class GroupDiscount
{
    private const int GroupSize = 10;
    private const decimal Discount = 0.10m;

    internal static decimal TicketsTotal(IReadOnlyList<decimal> ticketPrices)
    {
        var tickets = 0m;
        foreach (var price in ticketPrices)
        {
            tickets += price;
        }
        if (ticketPrices.Count >= GroupSize)
        {
            var discount = Math.Round(tickets * Discount, 2, MidpointRounding.AwayFromZero);
            tickets -= discount;
        }
        return tickets;
    }
}
