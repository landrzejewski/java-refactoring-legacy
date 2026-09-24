namespace Training.Workshop.M8.S12ExpandContract.Step3;

/// <summary>
/// Krok 3: backfill - MigrateAll uzupełnia Payload w starych wierszach (idempotentnie, z licznikiem).
/// Dopiero gdy nie ma wiersza bez Payload, można przestać czytać i pisać stary format.
/// </summary>
public sealed class BookingRepository
{
    private readonly BookingTable _table;

    public BookingRepository(BookingTable table)
    {
        _table = table;
    }

    public void Save(Booking booking)
    {
        _table.Put(booking.Id, new BookingTable.Row(CsvBookingFormat.Write(booking),
            BookingPayloadFormat.Write(booking)));
    }

    public Booking? Find(string id)
    {
        BookingTable.Row? row = _table.Get(id);
        return row is null ? null : Read(row);
    }

    /// <summary>Uzupełnia Payload tam, gdzie go brakuje; zwraca liczbę zmigrowanych wierszy.</summary>
    public int MigrateAll()
    {
        int migrated = 0;
        foreach (string id in _table.Ids())
        {
            BookingTable.Row row = _table.Get(id) ?? throw new InvalidOperationException("brak wiersza " + id);
            if (row.Payload == null)
            {
                _table.Put(id, new BookingTable.Row(row.Csv,
                    BookingPayloadFormat.Write(CsvBookingFormat.Read(row.Csv!))));
                migrated++;
            }
        }
        return migrated;
    }

    private static Booking Read(BookingTable.Row row)
    {
        return row.Payload != null
            ? BookingPayloadFormat.Read(row.Payload)
            : CsvBookingFormat.Read(row.Csv!);
    }
}
