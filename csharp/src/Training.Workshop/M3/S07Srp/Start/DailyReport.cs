using System.Globalization;
using System.Text;

namespace Training.Workshop.M3.S07Srp.Start;

/// <summary>
/// Start: raport dzienny obsługuje dwóch aktorów - księgowość (VAT, przychód brutto)
/// i marketing (hit dnia, liczba biletów). Obie części korzystają ze wspólnego helpera
/// <c>Revenue</c>. Gdy marketing poprosi "hit dnia licz bez baru", poprawka helpera
/// po cichu zmieni też sumę dla księgowości.
/// </summary>
public sealed class DailyReport
{
    public string Render(IReadOnlyList<Sale> sales)
    {
        var @out = new StringBuilder();
        // księgowość
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
        // marketing
        @out.Append("MARKETING\n");
        var byTitle = new SortedDictionary<string, decimal>(StringComparer.Ordinal);
        var sold = 0;
        foreach (var sale in sales)
        {
            byTitle[sale.Title] = byTitle.GetValueOrDefault(sale.Title) + Revenue(sale);
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

    private static decimal Revenue(Sale sale)
    {
        return sale.TicketRevenue + sale.BarRevenue;
    }

    private static decimal Net(decimal gross, int vatPercent)
    {
        return Math.Round(gross * 100 / (100 + vatPercent), 2, MidpointRounding.AwayFromZero);
    }
}
