namespace Training.Workshop.M8.S12ExpandContract.Start;

/// <summary>
/// Start: repozytorium zna tylko kolumnę Csv. Zmiana formatu "w miejscu" (zapis nowego formatu
/// do tej samej kolumny) uniemożliwiłaby wycofanie wydania - stara wersja nie odczyta danych.
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
        _table.Put(booking.Id, new BookingTable.Row(CsvBookingFormat.Write(booking), null));
    }

    public Booking? Find(string id)
    {
        string? csv = _table.Get(id)?.Csv;
        return csv is null ? null : CsvBookingFormat.Read(csv);
    }
}
