using System.Globalization;
using System.Text;

namespace Training.Workshop.M4.S01Rename.Step2;

/// <summary>
/// Krok 2: Rename metody Calc2 -&gt; RevenueCsv. IDE poprawiło wywołania w C#, ale nie konfigurację
/// ReportJob (tekst, a w produkcji plik na serwerze) - test zadania to wyłapał.
/// Strategia migracji: stara nazwa zostaje jako przestarzały delegat, dopóki konfiguracja jej używa.
/// </summary>
public sealed class SalesReport
{
    /// <summary>Wiersz raportu. Nazwy właściwości trafiają do nagłówka CSV.</summary>
    public sealed record Line(string t, int n, decimal d);

    /// <summary>
    /// Stara nazwa z konfiguracji report.properties (report.method=Calc2).
    /// Usunąć dopiero, gdy żaden serwer nie ma jej w konfiguracji.
    /// </summary>
    [Obsolete("Użyj RevenueCsv - Calc2 zostaje tylko dla konfiguracji report.properties")]
    public string Calc2(IReadOnlyList<Sale> sales, bool onlineOnly)
    {
        return RevenueCsv(sales, onlineOnly);
    }

    public string RevenueCsv(IReadOnlyList<Sale> sales, bool onlineOnly)
    {
        var linesByTitle = new SortedDictionary<string, Line>(StringComparer.Ordinal);
        foreach (var sale in sales)
        {
            if (onlineOnly && !sale.Online)
            {
                continue;
            }
            if (!linesByTitle.TryGetValue(sale.Title, out var previous))
            {
                linesByTitle[sale.Title] = new Line(sale.Title, sale.Tickets, sale.Amount);
            }
            else
            {
                linesByTitle[sale.Title] = new Line(sale.Title,
                    previous.n + sale.Tickets, previous.d + sale.Amount);
            }
        }
        var csv = new StringBuilder(Header()).Append('\n');
        foreach (var line in linesByTitle.Values)
        {
            csv.Append(Row(line)).Append('\n');
        }
        return csv.ToString();
    }

    private static string Header()
    {
        return string.Join(";", typeof(Line).GetProperties().Select(property => property.Name));
    }

    private static string Row(Line line)
    {
        var values = new List<string>();
        foreach (var property in typeof(Line).GetProperties())
        {
            values.Add(Convert.ToString(property.GetValue(line), CultureInfo.InvariantCulture) ?? "null");
        }
        return string.Join(";", values);
    }
}
