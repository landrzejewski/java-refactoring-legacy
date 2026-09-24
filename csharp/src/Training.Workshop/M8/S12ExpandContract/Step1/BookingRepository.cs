namespace Training.Workshop.M8.S12ExpandContract.Step1;

/// <summary>
/// Krok 1: expand + dual write - zapis do obu kolumn, odczyt nadal ze starej. Wycofanie do
/// poprzedniej wersji jest bezpieczne: stara wersja czyta Csv, który wciąż powstaje.
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
        string? csv = _table.Get(id)?.Csv;
        return csv is null ? null : CsvBookingFormat.Read(csv);
    }
}
