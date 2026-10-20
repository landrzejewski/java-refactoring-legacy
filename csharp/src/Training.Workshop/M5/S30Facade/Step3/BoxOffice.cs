using Training.Workshop.Shared;

namespace Training.Workshop.M5.S30Facade.Step3;

/// <summary>
/// Krok 3: bez zmian - kasa korzysta z tej samej fasady co sklep, bez opłaty rezerwacyjnej.
/// Skopiowana orkiestracja zniknęła razem z jej błędem (brak zwolnienia miejsc po odmowie).
/// </summary>
public sealed class BoxOffice
{
    private readonly BookingFacade _booking;

    public BoxOffice(BookingFacade booking)
    {
        ArgumentNullException.ThrowIfNull(booking);
        _booking = booking;
    }

    public string Sell(TicketOrder order)
    {
        return _booking.Book(order, Money.Zero);
    }
}
