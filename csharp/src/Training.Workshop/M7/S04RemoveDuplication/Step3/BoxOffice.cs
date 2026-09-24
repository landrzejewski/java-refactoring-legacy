namespace Training.Workshop.M7.S04RemoveDuplication.Step3;

/// <summary>Krok 3 (rozwiązanie): kasa korzysta ze wspólnej reguły GroupDiscount.</summary>
public sealed class BoxOffice
{
    public decimal Total(IReadOnlyList<decimal> ticketPrices)
    {
        return Math.Round(GroupDiscount.TicketsTotal(ticketPrices), 2, MidpointRounding.AwayFromZero);
    }
}
