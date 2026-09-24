namespace Training.Workshop.M8.S12ExpandContract.Step4;

/// <summary>
/// Krok 4: contract - po backfillu i zamknięciu okna wycofania usuwamy stary format (Safe Delete
/// klasy formatu, fallbacku i podwójnego zapisu). Kolejna migracja schematu usunie starą kolumnę.
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
        _table.Put(booking.Id, BookingTable.Row.WithPayload(BookingPayloadFormat.Write(booking)));
    }

    public Booking? Find(string id)
    {
        string? payload = _table.Get(id)?.Payload;
        return payload is null ? null : BookingPayloadFormat.Read(payload);
    }
}
