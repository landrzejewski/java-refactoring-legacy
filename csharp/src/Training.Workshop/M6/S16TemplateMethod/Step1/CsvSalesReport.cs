using System.Globalization;
using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S16TemplateMethod.Step1;

/// <summary>
/// Krok 1: Extract Method na różnicach (Header, Row, Footer) - w obu raportach te same nazwy
/// i sygnatury, więc Render() obu klas staje się identyczny.
/// </summary>
public sealed class CsvSalesReport
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
        return "godzina;film;bilety;kwota\n";
    }

    private static string Row(Sale sale)
    {
        var title = sale.Title.Contains(';') ? "\"" + sale.Title + "\"" : sale.Title;
        return sale.Time.ToString("HH:mm", CultureInfo.InvariantCulture) + ";" + title + ";" + sale.Tickets + ";" + sale.Amount + "\n";
    }

    private static string Footer(int tickets, Money total)
    {
        return "SUMA;;" + tickets + ";" + total + "\n";
    }
}
