using System.Globalization;

namespace Training.Workshop.M7.S03BreakResponsibilities.Step3;

/// <summary>
/// Krok 3 (rozwiązanie): BookingDesk tylko składa trzy kroki - waliduj, wyceń, powiadom.
/// Publiczne API (konstruktor z Outbox, Book) bez zmian; szczegóły mają osobnych właścicieli.
/// </summary>
public sealed class BookingDesk
{
    private readonly BookingValidator _validator;
    private readonly TicketPricer _pricer;
    private readonly BookingNotifier _notifier;

    public BookingDesk(Outbox outbox)
        : this(new BookingValidator(), new TicketPricer(), new BookingNotifier(outbox))
    {
    }

    internal BookingDesk(BookingValidator validator, TicketPricer pricer, BookingNotifier notifier)
    {
        ArgumentNullException.ThrowIfNull(validator);
        ArgumentNullException.ThrowIfNull(pricer);
        ArgumentNullException.ThrowIfNull(notifier);
        _validator = validator;
        _pricer = pricer;
        _notifier = notifier;
    }

    public string Book(BookingRequest request)
    {
        var error = _validator.FirstError(request);
        if (error != null)
        {
            return error;
        }
        var pricing = _pricer.Price(request);
        _notifier.BookingConfirmed(request, pricing);
        return "OK " + pricing.Total.ToString(CultureInfo.InvariantCulture);
    }
}
