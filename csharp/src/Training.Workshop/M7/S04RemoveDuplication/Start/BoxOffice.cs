namespace Training.Workshop.M7.S04RemoveDuplication.Start;

/// <summary>
/// Start: kasa liczy rabat grupowy (10+ biletów = -10%). Ta sama reguła żyje też w WebShop -
/// inaczej zapisana i z innym zaokrągleniem. To duplikacja wiedzy, nie tylko tekstu.
/// </summary>
public sealed class BoxOffice
{
    public decimal Total(IReadOnlyList<decimal> ticketPrices)
    {
        var sum = 0m;
        foreach (var price in ticketPrices)
        {
            sum += price;
        }
        if (ticketPrices.Count > 9)
        {
            var discount = Math.Round(sum * 0.10m, 2, MidpointRounding.AwayFromZero);
            sum -= discount;
        }
        return Math.Round(sum, 2, MidpointRounding.AwayFromZero);
    }
}
