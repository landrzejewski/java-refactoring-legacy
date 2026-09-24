using System.Globalization;
using System.Text;

namespace Training.Workshop.M4.S01Rename.Step3;

/// <summary>
/// Krok 3: Rename właściwości wiersza t, n, d -&gt; Title, Tickets, Revenue.
/// Nagłówek CSV był wyliczany refleksją z nazw C#, więc sam Rename zmieniłby plik
/// dla dystrybutora. Dlatego odcinamy kontrakt zewnętrzny od nazw w kodzie:
/// jawny nagłówek i jawny wiersz.
/// </summary>
public sealed class SalesReport
{
    /// <summary>Nagłówek uzgodniony z dystrybutorem - kontrakt zewnętrzny, NIE nazwy właściwości w C#.</summary>
    internal const string CsvHeader = "t;n;d";

    public sealed record Line(string Title, int Tickets, decimal Revenue);

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
                    previous.Tickets + sale.Tickets, previous.Revenue + sale.Amount);
            }
        }
        var csv = new StringBuilder(CsvHeader).Append('\n');
        foreach (var line in linesByTitle.Values)
        {
            csv.Append(Row(line)).Append('\n');
        }
        return csv.ToString();
    }

    private static string Row(Line line)
    {
        return line.Title + ";" + line.Tickets + ";" + line.Revenue.ToString(CultureInfo.InvariantCulture);
    }
}
