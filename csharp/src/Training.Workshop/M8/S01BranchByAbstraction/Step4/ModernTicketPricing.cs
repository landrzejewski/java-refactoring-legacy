using System.Globalization;
using Training.Workshop.Shared;

namespace Training.Workshop.M8.S01BranchByAbstraction.Step4;

/// <summary>
/// Krok 4: jedyna implementacja cennika - Money, nazwane reguły, jawne kody.
/// Stara implementacja zniknęła razem z przełącznikiem.
/// </summary>
public sealed class ModernTicketPricing : ITicketPricing
{
    private static readonly Money MorningDiscount = Money.Of("5.00");
    private static readonly Money VipSurcharge = Money.Of("10.00");
    private static readonly Money Glasses3D = Money.Of("3.00");
    private static readonly Money OnlineFee = Money.Of("2.00");
    private const int GroupSize = 10;

    public Money Total(BookingRequest request)
    {
        Money tickets = Money.Zero;
        for (int i = 0; i < request.Seats.Count; i++)
        {
            tickets = tickets.Plus(Ticket(request, request.Seats[i], request.Types[i]));
        }
        int count = request.Seats.Count;
        if (count >= GroupSize)
        {
            tickets = tickets.Minus(tickets.Percent(10));
        }
        Money fees = request.Web ? OnlineFee.Times(count) : Money.Zero;
        return tickets.Plus(fees);
    }

    private static Money Ticket(BookingRequest request, string seat, string type)
    {
        Screening screening = request.Screening;
        Money @base = BasePrice(screening.Format);
        Money price = @base.Minus(@base.Percent(DiscountPercent(type)));
        if (screening.Start.Hour < 12)
        {
            price = price.Minus(MorningDiscount);
        }
        if (int.Parse(seat.Substring(1), CultureInfo.InvariantCulture) >= screening.VipFromRow)
        {
            price = price.Plus(VipSurcharge);
        }
        if (screening.Format == 2 && !request.OwnGlasses)
        {
            price = price.Plus(Glasses3D);
        }
        return price;
    }

    private static Money BasePrice(int format) => format switch
    {
        1 => Money.Of("25.00"),
        2 => Money.Of("32.00"),
        3 => Money.Of("40.00"),
        _ => throw new ArgumentException("Nieznany format: " + format),
    };

    private static int DiscountPercent(string type) => type switch
    {
        "S" => 25,
        "E" => 30,
        "C" => 40,
        _ => 0,
    };
}
