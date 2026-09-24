namespace Training.Workshop.M3.S14ReversiblePattern.Start;

/// <summary>
/// Start: dwa istniejące modele rozliczeń w jednym switchu. Model festiwalowy
/// dodatkowo tłumaczy obcy interfejs (grosze w long) w środku logiki rozliczeń.
/// Oba warianty istnieją dziś i zmieniają się niezależnie - to uzasadnia wzorzec.
/// </summary>
public sealed class DistributorSettlement
{
    private readonly FestivalTariffClient _festival = new();

    public decimal Payout(Deal deal, int week, decimal ticketRevenue)
    {
        switch (deal.Model)
        {
            case "PERCENT":
            {
                var percent = week == 1 ? 50 : week == 2 ? 40 : 35;
                var share = Math.Round(ticketRevenue * percent / 100, 2, MidpointRounding.AwayFromZero);
                return Math.Max(share, 500.00m);
            }
            case "FESTIVAL":
            {
                var cents = _festival.WeeklyFeeInCents(deal.Title, week);
                return cents / 100m;
            }
            default:
                throw new ArgumentException("nieznany model: " + deal.Model);
        }
    }
}
