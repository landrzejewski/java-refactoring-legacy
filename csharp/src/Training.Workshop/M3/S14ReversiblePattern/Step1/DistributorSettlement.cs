namespace Training.Workshop.M3.S14ReversiblePattern.Step1;

/// <summary>
/// Krok 1: Replace Conditional with Strategy. Rozliczenie tylko wybiera model;
/// każdy wariant zmienia się we własnej klasie. Uzasadnienie: dwa ISTNIEJĄCE warianty
/// z różnymi właścicielami i obcy interfejs festiwalu - nie przyszłe pluginy.
/// </summary>
public sealed class DistributorSettlement
{
    private readonly IReadOnlyDictionary<string, ISettlementModel> _models = new Dictionary<string, ISettlementModel>
    {
        ["PERCENT"] = new PercentageModel(),
        ["FESTIVAL"] = new FestivalFeeAdapter(new FestivalTariffClient()),
    };

    public decimal Payout(Deal deal, int week, decimal ticketRevenue)
    {
        if (!_models.TryGetValue(deal.Model, out var model))
        {
            throw new ArgumentException("nieznany model: " + deal.Model);
        }
        return model.Payout(deal, week, ticketRevenue);
    }
}
