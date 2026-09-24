using System.Globalization;

namespace Training.Workshop.M7.S03BreakResponsibilities.Step3;

/// <summary>Krok 2: Extract Class - cennik ma jednego właściciela i zwraca wynik zamiast dwóch zmiennych.</summary>
internal sealed class TicketPricer
{
    internal Pricing Price(BookingRequest request)
    {
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
        return new Pricing(total, vipSeats);
    }
}
