using System.Globalization;
using System.Text;

namespace Training.Workshop.M4.S01Rename.Start;

/// <summary>
/// Start: raport sprzedaży dla dystrybutora (CSV). Nazwy nic nie mówią: Calc2, s, flag, m, x, l,
/// a właściwości wiersza to t, n, d. Dwie nazwy żyją też POZA C#:
/// "Calc2" w konfiguracji ReportJob (refleksja) i t/n/d w nagłówku CSV (refleksja po rekordzie).
/// </summary>
public sealed class SalesReport
{
    /// <summary>Wiersz raportu. Nazwy właściwości trafiają do nagłówka CSV.</summary>
    public sealed record Line(string t, int n, decimal d);

    /// <summary>Wywoływana także refleksyjnie przez ReportJob - nazwa metody jest w konfiguracji.</summary>
    public string Calc2(IReadOnlyList<Sale> s, bool flag)
    {
        var m = new SortedDictionary<string, Line>(StringComparer.Ordinal);
        foreach (var x in s)
        {
            if (flag && !x.Online)
            {
                continue;
            }
            if (!m.TryGetValue(x.Title, out var l))
            {
                m[x.Title] = new Line(x.Title, x.Tickets, x.Amount);
            }
            else
            {
                m[x.Title] = new Line(x.Title, l.n + x.Tickets, l.d + x.Amount);
            }
        }
        var b = new StringBuilder(Header()).Append('\n');
        foreach (var l in m.Values)
        {
            b.Append(Row(l)).Append('\n');
        }
        return b.ToString();
    }

    private static string Header()
    {
        return string.Join(";", typeof(Line).GetProperties().Select(p => p.Name));
    }

    private static string Row(Line l)
    {
        var v = new List<string>();
        foreach (var c in typeof(Line).GetProperties())
        {
            v.Add(Convert.ToString(c.GetValue(l), CultureInfo.InvariantCulture) ?? "null");
        }
        return string.Join(";", v);
    }
}
