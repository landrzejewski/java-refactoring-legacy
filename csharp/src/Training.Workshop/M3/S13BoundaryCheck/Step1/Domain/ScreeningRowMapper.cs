using System.Data.SqlTypes;
using Training.Workshop.M3.S13BoundaryCheck.Step1.Adapter;

namespace Training.Workshop.M3.S13BoundaryCheck.Step1.Domain;

/// <summary>
/// Krok 1: Extract Class - mapowanie na wiersz jako osobna, spójna klasa (LCOM4 = 1).
/// Wciąż leży w Domain, więc test granicy nadal jest czerwony: spójność poprawiona,
/// kierunek zależności jeszcze nie.
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
