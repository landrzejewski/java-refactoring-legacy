using System.Globalization;
using System.Text;

namespace Training.Workshop.M4.S01Rename.Step1;

/// <summary>
/// Krok 1: Rename zmiennych lokalnych i parametrów (s, flag, m, x, l, b, v, c).
/// Zasięg lokalny, brak użyć poza metodą - IDE robi to w pełni bezpiecznie.
/// </summary>
public sealed class SalesReport
{
    /// <summary>Wiersz raportu. Nazwy właściwości trafiają do nagłówka CSV.</summary>
    public sealed record Line(string t, int n, decimal d);

    /// <summary>Wywoływana także refleksyjnie przez ReportJob - nazwa metody jest w konfiguracji.</summary>
    public string Calc2(IReadOnlyList<Sale> sales, bool onlineOnly)
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
