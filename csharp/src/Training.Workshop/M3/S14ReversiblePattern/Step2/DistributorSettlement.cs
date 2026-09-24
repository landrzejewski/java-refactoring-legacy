namespace Training.Workshop.M3.S14ReversiblePattern.Step2;

/// <summary>
/// Krok 2: wariant znika - umowy festiwalowe wygasły. Safe Delete FestivalFeeAdapter
/// i wpisu w słowniku. Zostaje Strategy z JEDNĄ implementacją: sygnał nadmiaru wzorca.
/// (To zmiana zachowania: model FESTIVAL jest teraz odrzucany.)
/// </summary>
public sealed class DistributorSettlement
{
    private readonly IReadOnlyDictionary<string, ISettlementModel> _models = new Dictionary<string, ISettlementModel>
    {
        ["PERCENT"] = new PercentageModel(),
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
