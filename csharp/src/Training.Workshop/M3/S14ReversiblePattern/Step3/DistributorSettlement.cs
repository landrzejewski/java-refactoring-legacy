namespace Training.Workshop.M3.S14ReversiblePattern.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): refaktoryzacja OD wzorca. Inline Class PercentageModel,
/// Safe Delete interfejsu ISettlementModel i słownika. Jeden algorytm = jedna metoda.
/// Gdy wróci drugi model, Strategy da się przywrócić tymi samymi krokami w przód.
/// </summary>
public sealed class DistributorSettlement
{
    private const decimal MinimumGuarantee = 500.00m;

    public decimal Payout(Deal deal, int week, decimal ticketRevenue)
    {
        if (deal.Model != "PERCENT")
        {
            throw new ArgumentException("nieznany model: " + deal.Model);
        }
        var percent = week == 1 ? 50 : week == 2 ? 40 : 35;
        var share = Math.Round(ticketRevenue * percent / 100, 2, MidpointRounding.AwayFromZero);
        return Math.Max(share, MinimumGuarantee);
    }
}
