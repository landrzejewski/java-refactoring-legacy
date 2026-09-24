using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S16TemplateMethod.Step2;

/// <summary>Krok 2: raport HTML to już tylko trzy metody formatujące.</summary>
public sealed class HtmlSalesReport : SalesReport
{
    protected override string Header()
    {
        return "<table>\n<tr><th>Godzina</th><th>Film</th><th>Bilety</th><th>Kwota</th></tr>\n";
    }

    protected override string Row(Sale sale)
    {
        var title = sale.Title.Replace("&", "&amp;").Replace("<", "&lt;");
        return "<tr><td>" + sale.Time.ToString("HH:mm", CultureInfo.InvariantCulture) + "</td><td>" + title + "</td><td>" + sale.Tickets
            + "</td><td>" + sale.Amount + "</td></tr>\n";
    }

    protected override string Footer(int tickets, Money total)
    {
        return "<tr><td colspan=\"2\">Suma</td><td>" + tickets + "</td><td>" + total + "</td></tr>\n"
            + "</table>\n";
    }
}
