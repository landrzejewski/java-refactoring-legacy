namespace Training.Workshop.M3.S07Srp.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): Extract Class - każda sekcja w klasie swojego aktora.
/// DailyReport tylko składa dokument (koordynuje, nie zna polityk). Zmiana definicji
/// hitu dotyka wyłącznie <see cref="MarketingSection"/>.
/// </summary>
public sealed class DailyReport
{
    private readonly AccountingSection _accounting = new();
    private readonly MarketingSection _marketing = new();

    public string Render(IReadOnlyList<Sale> sales)
    {
        return _accounting.Render(sales) + _marketing.Render(sales);
    }
}
