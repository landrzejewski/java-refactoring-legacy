using System.Globalization;
using System.Text;

namespace Training.Workshop.M3.S12CleanArchitecture;

/// <summary>
/// Świat zewnętrzny sceny (stabilny): "baza danych" z wierszami object[].
/// Niedostępna baza rzuca <see cref="InvalidOperationException"/> przy zapisie.
/// </summary>
public sealed class RowStore
{
    private readonly bool _available;
    private readonly List<object[]> _rows = [];

    public RowStore(bool available)
    {
        _available = available;
    }

    /// <summary>Zapisuje wiersz i zwraca wygenerowany identyfikator R-n.</summary>
    public string Insert(object[] columns)
    {
        if (!_available)
        {
            throw new InvalidOperationException("baza niedostepna");
        }
        _rows.Add((object[])columns.Clone());
        return "R-" + _rows.Count;
    }

    public IReadOnlyList<string> Dump()
    {
        var result = new List<string>();
        foreach (var row in _rows)
        {
            var line = new StringBuilder();
            foreach (var column in row)
            {
                line.Append(line.Length == 0 ? "" : ";").Append(Convert.ToString(column, CultureInfo.InvariantCulture));
            }
            result.Add(line.ToString());
        }
        return result;
    }
}
