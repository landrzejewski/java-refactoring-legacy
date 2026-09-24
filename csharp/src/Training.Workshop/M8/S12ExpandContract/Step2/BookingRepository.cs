namespace Training.Workshop.M8.S12ExpandContract.Step2;

/// <summary>
/// Krok 2: odczyt z nowej kolumny z fallbackiem - wiersze sprzed kroku 1 mają tylko Csv.
/// Zapis nadal podwójny, więc wycofanie do kroku 1 albo startu wciąż jest możliwe.
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

    private static Booking Read(BookingTable.Row row)
    {
        return row.Payload != null
            ? BookingPayloadFormat.Read(row.Payload)
            : CsvBookingFormat.Read(row.Csv!);
    }
}
