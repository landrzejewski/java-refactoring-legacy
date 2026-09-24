using System.Globalization;
using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S16TemplateMethod.Start;

/// <summary>Start: raport HTML - ten sam szkielet co CSV, napisany trochę inaczej.</summary>
public sealed class HtmlSalesReport
{
    public string Render(IReadOnlyList<Sale> sales)
    {
        var html = new StringBuilder();
        html.Append("<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n");
        var sum = Money.Zero;
        var count = 0;
        foreach (var sale in sales.OrderBy(sale => sale.Time))
        {
            var title = sale.Title.Replace("&", "&amp;").Replace("<", "&lt;");
            html.Append("<tr><td>").Append(sale.Time.ToString("HH:mm", CultureInfo.InvariantCulture))
                .Append("</td><td>").Append(title)
                .Append("</td><td>").Append(sale.Tickets).Append("</td><td>").Append(sale.Amount)
                .Append("</td></tr>\n");
            sum = sum.Plus(sale.Amount);
            count += sale.Tickets;
        }
        html.Append("<tr><td colspan=\"2\">Suma</td><td>").Append(count).Append("</td><td>").Append(sum)
            .Append("</td></tr>\n</table>\n");
        return html.ToString();
    }
}
