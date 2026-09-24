using System.Globalization;

namespace Training.Workshop.M7.S03BreakResponsibilities.Step1;

/// <summary>Krok 1: Extract Class dla walidacji - BookingDesk deleguje do BookingValidator.</summary>
public sealed class BookingDesk
{
    private readonly Outbox _outbox;
    private readonly BookingValidator _validator = new();

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

        // wycena
        var basePrice = request.Format switch
        {
            "IMAX" => 40.00m,
            "3D" => 32.00m,
            _ => 25.00m,
        };
        var total = 0m;
        var vipSeats = 0;
        foreach (var seat in request.Seats)
        {
            total += basePrice;
            if (int.Parse(seat[1..], CultureInfo.InvariantCulture) >= 10)
            {
                total += 10.00m;
                vipSeats++;
            }
        }

        // powiadomienie
        var text = "Rezerwacja " + request.Seats.Count + " miejsc";
        if (vipSeats > 0)
        {
            text = text + " (VIP: " + vipSeats + ")";
        }
        _outbox.Send(request.Email!, text + ", do zaplaty " + total.ToString(CultureInfo.InvariantCulture));
        return "OK " + total.ToString(CultureInfo.InvariantCulture);
    }
}
