namespace Training.Workshop.M7.S04RemoveDuplication.Step2;

/// <summary>
/// Krok 2: świadoma decyzja (zmiana kontraktu, osobny commit) - biznes potwierdził,
/// że rabat zaokrąglamy AwayFromZero (HALF_UP) jak w kasie. Dla rabatu z końcówką 5 na trzecim
/// miejscu po przecinku wynik online się zmienia.
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
            var discount = Math.Round(tickets * GroupDiscount, 2, MidpointRounding.AwayFromZero);
            tickets -= discount;
        }
        var fees = Fee * ticketPrices.Count;
        return Math.Round(tickets + fees, 2, MidpointRounding.AwayFromZero);
    }
}
