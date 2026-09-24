using System.Globalization;
using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S16TemplateMethod.Start;

/// <summary>Start: raport CSV - szkielet (sortowanie, nagłówek, wiersze, suma) skopiowany w raporcie HTML.</summary>
public sealed class CsvSalesReport
{
    public string Render(IReadOnlyList<Sale> sales)
    {
        var sorted = sales.OrderBy(sale => sale.Time).ToList();
        var csv = new StringBuilder("godzina;film;bilety;kwota\n");
        var tickets = 0;
        var total = Money.Zero;
        foreach (var sale in sorted)
        {
            var title = sale.Title.Contains(';') ? "\"" + sale.Title + "\"" : sale.Title;
            csv.Append(sale.Time.ToString("HH:mm", CultureInfo.InvariantCulture)).Append(';').Append(title).Append(';')
                .Append(sale.Tickets).Append(';').Append(sale.Amount).Append('\n');
            tickets += sale.Tickets;
            total = total.Plus(sale.Amount);
        }
        csv.Append("SUMA;;").Append(tickets).Append(';').Append(total).Append('\n');
        return csv.ToString();
    }
}
