namespace Training.Workshop.M3.S14ReversiblePattern.Step1;

/// <summary>
/// Krok 1: Adapter - tłumaczy obcy interfejs (grosze w long) na kontrakt
/// <see cref="ISettlementModel"/>. Ma realną pracę: konwersję jednostek. Przychód z biletów
/// ignoruje - w tym modelu opłata nie zależy od sprzedaży.
/// </summary>
public sealed class FestivalFeeAdapter : ISettlementModel
{
    private readonly FestivalTariffClient _client;

    public FestivalFeeAdapter(FestivalTariffClient client)
    {
        _client = client;
    }

    public decimal Payout(Deal deal, int week, decimal ticketRevenue)
    {
        return _client.WeeklyFeeInCents(deal.Title, week) / 100m;
    }
}
