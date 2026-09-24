using Training.Workshop.Shared;

namespace Training.Workshop.M8.S02StranglerFig.Step4;

/// <summary>Krok 4 (bez zmian): nowy moduł rezerwacji - Money, nazwane reguły, wspólna baza z legacy.</summary>
public sealed class BookingModule
{
    private static readonly Money OnlineFee = Money.Of("2.00");
    private const int GroupSize = 10;

    private readonly BookingLedger _ledger;

    public BookingModule(BookingLedger ledger)
    {
        _ledger = ledger;
    }

    public string Book(string email, string title, int format, int tickets, bool web)
    {
        if (tickets <= 0)
        {
            return "ERROR: no seats";
        }
        Money value = BasePrice(format).Times(tickets);
        if (tickets >= GroupSize)
        {
            value = value.Minus(value.Percent(10));
        }
        Money fees = web ? OnlineFee.Times(tickets) : Money.Zero;
        string id = _ledger.NextId();
        _ledger.Add(new BookingLedger.Booking(id, email, title, tickets, value, fees));
        return id;
    }

    private static Money BasePrice(int format) => format switch
    {
        1 => Money.Of("25.00"),
        2 => Money.Of("32.00"),
        _ => Money.Of("40.00"),
    };
}
