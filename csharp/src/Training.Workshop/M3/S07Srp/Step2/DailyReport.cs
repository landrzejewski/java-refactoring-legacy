using System.Globalization;
using System.Text;

namespace Training.Workshop.M3.S07Srp.Step2;

/// <summary>
/// Krok 2: rozdzielenie wspólnego helpera według aktora. Marketing dostaje własną
/// <c>Popularity</c> - dziś liczoną tak samo jak <c>Revenue</c>, ale to inna wiedza
/// z innym właścicielem. Świadome powtórzenie kodu, nie wiedzy.
/// </summary>
public sealed class DailyReport
{
    public string Render(IReadOnlyList<Sale> sales)
    {
        return AccountingSection(sales) + MarketingSection(sales);
    }

    private static string AccountingSection(IReadOnlyList<Sale> sales)
    {
        var @out = new StringBuilder();
        var tickets = 0.00m;
        var bar = 0.00m;
        foreach (var sale in sales)
        {
            tickets += sale.TicketRevenue;
            bar += sale.BarRevenue;
        }
        @out.Append("KSIEGOWOSC\n");
        @out.Append(CultureInfo.InvariantCulture, $"Bilety brutto {tickets:0.00}, netto {Net(tickets, 8):0.00}\n");
        @out.Append(CultureInfo.InvariantCulture, $"Bar brutto {bar:0.00}, netto {Net(bar, 23):0.00}\n");
        var total = 0.00m;
        foreach (var sale in sales)
        {
            total += Revenue(sale);
        }
        @out.Append(CultureInfo.InvariantCulture, $"Razem brutto {total:0.00}\n");
        return @out.ToString();
    }

    private static string MarketingSection(IReadOnlyList<Sale> sales)
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

    /// <summary>Księgowość: przychód brutto seansu.</summary>
    private static decimal Revenue(Sale sale)
    {
        return sale.TicketRevenue + sale.BarRevenue;
    }

    /// <summary>Marketing: miara popularności filmu (dziś: bilety + bar).</summary>
    private static decimal Popularity(Sale sale)
    {
        return sale.TicketRevenue + sale.BarRevenue;
    }

    private static decimal Net(decimal gross, int vatPercent)
    {
        return Math.Round(gross * 100 / (100 + vatPercent), 2, MidpointRounding.AwayFromZero);
    }
}
