using System.Globalization;
using System.Text;

namespace Training.Workshop.M3.S07Srp.Step3;

/// <summary>Krok 3: aktor - marketing. Zmienia się, gdy marketing zmienia definicję "hitu".</summary>
internal sealed class MarketingSection
{
    internal string Render(IReadOnlyList<Sale> sales)
    {
        var @out = new StringBuilder();
        @out.Append("MARKETING\n");
        var byTitle = new SortedDictionary<string, decimal>(StringComparer.Ordinal);
        var sold = 0;
        foreach (var sale in sales)
        {
            byTitle[sale.Title] = byTitle.GetValueOrDefault(sale.Title) + Popularity(sale);
            sold += sale.Tickets;
        }
        var hit = "brak";
        var best = 0m;
        foreach (var (title, amount) in byTitle)
        {
            if (amount > best)
            {
                hit = string.Create(CultureInfo.InvariantCulture, $"{title} ({amount:0.00})");
                best = amount;
            }
        }
        @out.Append("Hit dnia: ").Append(hit).Append('\n');
        @out.Append("Sprzedanych biletow: ").Append(sold).Append('\n');
        return @out.ToString();
    }

    private static decimal Popularity(Sale sale)
    {
        return sale.TicketRevenue + sale.BarRevenue;
    }
}
