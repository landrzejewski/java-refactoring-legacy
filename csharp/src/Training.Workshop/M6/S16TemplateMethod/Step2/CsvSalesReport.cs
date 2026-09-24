using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S16TemplateMethod.Step2;

/// <summary>Krok 2: raport CSV to już tylko trzy metody formatujące.</summary>
public sealed class CsvSalesReport : SalesReport
{
    protected override string Header()
    {
        return "godzina;film;bilety;kwota\n";
    }

    protected override string Row(Sale sale)
    {
        var title = sale.Title.Contains(';') ? "\"" + sale.Title + "\"" : sale.Title;
        return sale.Time.ToString("HH:mm", CultureInfo.InvariantCulture) + ";" + title + ";" + sale.Tickets + ";" + sale.Amount + "\n";
    }

    protected override string Footer(int tickets, Money total)
    {
        return "SUMA;;" + tickets + ";" + total + "\n";
    }
}
