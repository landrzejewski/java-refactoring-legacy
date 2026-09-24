namespace Training.Workshop.M3.S14ReversiblePattern.Step1;

/// <summary>Krok 1: procent od przychodu - tydzień 1: 50%, 2: 40%, dalej 35%; minimalna gwarancja 500.00.</summary>
public sealed class PercentageModel : ISettlementModel
{
    private const decimal MinimumGuarantee = 500.00m;

    public decimal Payout(Deal deal, int week, decimal ticketRevenue)
    {
        var percent = week == 1 ? 50 : week == 2 ? 40 : 35;
        var share = Math.Round(ticketRevenue * percent / 100, 2, MidpointRounding.AwayFromZero);
        return Math.Max(share, MinimumGuarantee);
    }
}
