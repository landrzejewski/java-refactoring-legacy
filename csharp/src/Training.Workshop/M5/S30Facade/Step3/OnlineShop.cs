using Training.Workshop.Shared;

namespace Training.Workshop.M5.S30Facade.Step3;

/// <summary>Krok 3: bez zmian - sklep deleguje cały zakup do fasady; zna już tylko swoją opłatę rezerwacyjną.</summary>
public sealed class OnlineShop
{
    private static readonly Money BookingFee = Money.Of("2.00");

    private readonly BookingFacade _booking;

    public OnlineShop(BookingFacade booking)
    {
        ArgumentNullException.ThrowIfNull(booking);
        _booking = booking;
    }

    public string Buy(TicketOrder order)
    {
        return _booking.Book(order, BookingFee);
    }
}
