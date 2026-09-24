using System.Globalization;
using System.Text.RegularExpressions;

namespace Training.Workshop.M7.S03BreakResponsibilities.Start;

/// <summary>
/// Start: jedna metoda, trzy powody zmiany - reguły walidacji (dział obsługi),
/// cennik (dział finansów) i treść powiadomienia (marketing). Komentarze dzielą ją na klastry.
/// </summary>
public sealed class BookingDesk
{
    private readonly Outbox _outbox;

    public BookingDesk(Outbox outbox)
    {
        ArgumentNullException.ThrowIfNull(outbox);
        _outbox = outbox;
    }

    public string Book(BookingRequest request)
    {
        // walidacja
        if (request.Email == null || !request.Email.Contains('@'))
        {
            return "ERROR: niepoprawny e-mail";
        }
        if (request.Seats.Count == 0)
        {
            return "ERROR: brak miejsc";
        }
        foreach (var seat in request.Seats)
        {
            if (!Regex.IsMatch(seat, "^[A-L][0-9]{1,2}$"))
            {
                return "ERROR: niepoprawne miejsce " + seat;
            }
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
        _outbox.Send(request.Email, text + ", do zaplaty " + total.ToString(CultureInfo.InvariantCulture));
        return "OK " + total.ToString(CultureInfo.InvariantCulture);
    }
}
