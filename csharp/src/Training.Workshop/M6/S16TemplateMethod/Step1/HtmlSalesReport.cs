using System.Globalization;
using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S16TemplateMethod.Step1;

/// <summary>Krok 1: te same metody co w CSV; Render() przepisany na identyczny szkielet.</summary>
public sealed class HtmlSalesReport
{
    public string Render(IReadOnlyList<Sale> sales)
    {
        var sorted = sales.OrderBy(sale => sale.Time).ToList();
        var text = new StringBuilder(Header());
        var tickets = 0;
        var total = Money.Zero;
        foreach (var sale in sorted)
        {
            text.Append(Row(sale));
            tickets += sale.Tickets;
            total = total.Plus(sale.Amount);
        }
        return text.Append(Footer(tickets, total)).ToString();
    }

    private static string Header()
    {
        return "<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n";
    }

    private static string Row(Sale sale)
    {
        var title = sale.Title.Replace("&", "&amp;").Replace("<", "&lt;");
        return "<tr><td>" + sale.Time.ToString("HH:mm", CultureInfo.InvariantCulture) + "</td><td>" + title + "</td><td>" + sale.Tickets
            + "</td><td>" + sale.Amount + "</td></tr>\n";
    }

    private static string Footer(int tickets, Money total)
    {
        return "<tr><td colspan=\"2\">Suma</td><td>" + tickets + "</td><td>" + total + "</td></tr>\n"
            + "</table>\n";
    }
}
