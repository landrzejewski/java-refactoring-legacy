namespace Training.Workshop.M8.S12ExpandContract;

/// <summary>
/// Tabela rezerwacji po migracji schematu "expand": obok starej kolumny <c>Csv</c> jest nowa,
/// dopuszczająca null kolumna <c>Payload</c>. Samo dodanie kolumny nic nie psuje - stary kod
/// jej nie zna. Usunięcie kolumny Csv to osobna migracja po zamknięciu okna wycofania.
/// </summary>
public sealed class BookingTable
{
    /// <summary>Wiersz: stara kolumna Csv i nowa kolumna Payload (każda może być null).</summary>
    public sealed record Row(string? Csv, string? Payload)
    {
        public static Row WithPayload(string payload)
        {
            return new Row(null, payload);
        }
    }

    private readonly SortedDictionary<string, Row> _rows = new(StringComparer.Ordinal);

    public void Put(string id, Row row)
    {
        _rows[id] = row;
    }

    public Row? Get(string id)
    {
        return _rows.GetValueOrDefault(id);
    }

    public IReadOnlyList<string> Ids()
    {
        return _rows.Keys.ToList();
    }
}
