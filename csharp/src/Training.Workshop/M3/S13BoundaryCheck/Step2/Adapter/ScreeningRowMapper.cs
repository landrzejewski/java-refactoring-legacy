using System.Data.SqlTypes;
using Training.Workshop.M3.S13BoundaryCheck.Step2.Domain;

namespace Training.Workshop.M3.S13BoundaryCheck.Step2.Adapter;

/// <summary>
/// Krok 2 (rozwiązanie): Move Class do Adapter. Zależność biegnie Adapter -&gt; Domain,
/// domena nie zna System.Data.SqlTypes ani wiersza tabeli. Test granicy zielony.
/// </summary>
public sealed class ScreeningRowMapper
{
    private readonly string _table;

    public ScreeningRowMapper(string table)
    {
        _table = table;
    }

    public ScreeningRow ToRow(Screening screening)
    {
        return new ScreeningRow(_table, screening.Title, new SqlDateTime(screening.Start));
    }

    public Screening FromRow(ScreeningRow row)
    {
        if (row.Table != _table)
        {
            throw new ArgumentException("obca tabela: " + row.Table);
        }
        return new Screening(row.Title, row.Start.Value);
    }
}
