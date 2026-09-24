using System.Globalization;

namespace Training.Workshop.M7.S03BreakResponsibilities.Step2;

/// <summary>Krok 2: Extract Class dla wyceny - BookingDesk deleguje do TicketPricer.</summary>
public sealed class BookingDesk
{
    private readonly Outbox _outbox;
    private readonly BookingValidator _validator = new();
    private readonly TicketPricer _pricer = new();

    public BookingDesk(Outbox outbox)
    {
        ArgumentNullException.ThrowIfNull(outbox);
        _outbox = outbox;
    }

    public string Book(BookingRequest request)
    {
        var error = _validator.FirstError(request);
        if (error != null)
        {
            return error;
        }
        var pricing = _pricer.Price(request);

        // powiadomienie
        var text = "Rezerwacja " + request.Seats.Count + " miejsc";
        if (pricing.VipSeats > 0)
        {
            text = text + " (VIP: " + pricing.VipSeats + ")";
        }
        _outbox.Send(request.Email!, text + ", do zaplaty " + pricing.Total.ToString(CultureInfo.InvariantCulture));
        return "OK " + pricing.Total.ToString(CultureInfo.InvariantCulture);
    }
}
