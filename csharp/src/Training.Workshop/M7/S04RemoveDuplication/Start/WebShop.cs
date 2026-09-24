namespace Training.Workshop.M7.S04RemoveDuplication.Start;

/// <summary>
/// Start: sklep internetowy - ta sama reguła rabatu grupowego co w BoxOffice, ale zapisana
/// inaczej (LINQ, x*10/100) i zaokrąglana ToEven (HALF_EVEN). Czy to celowa różnica, czy przypadek?
/// </summary>
public sealed class WebShop
{
    private const decimal Fee = 2.00m;

    public decimal Total(IReadOnlyList<decimal> prices)
    {
        var tickets = prices.Sum();
        if (prices.Count >= 10)
        {
            tickets -= Math.Round(tickets * 10 / 100, 2, MidpointRounding.ToEven);
        }
        var fees = Fee * prices.Count;
        return Math.Round(tickets + fees, 2, MidpointRounding.AwayFromZero);
    }
}
