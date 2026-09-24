using System.Globalization;

namespace Training.Workshop.M7.S03BreakResponsibilities.Step3;

/// <summary>Krok 3: Extract Class - treść i wysyłka powiadomienia mają jednego właściciela.</summary>
internal sealed class BookingNotifier
{
    private readonly Outbox _outbox;

    internal BookingNotifier(Outbox outbox)
    {
        ArgumentNullException.ThrowIfNull(outbox);
        _outbox = outbox;
    }

    internal void BookingConfirmed(BookingRequest request, Pricing pricing)
    {
        var text = "Rezerwacja " + request.Seats.Count + " miejsc";
        if (pricing.VipSeats > 0)
        {
            text = text + " (VIP: " + pricing.VipSeats + ")";
        }
        _outbox.Send(request.Email!, text + ", do zaplaty " + pricing.Total.ToString(CultureInfo.InvariantCulture));
    }
}
