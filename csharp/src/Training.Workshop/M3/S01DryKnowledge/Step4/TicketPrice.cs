namespace Training.Workshop.M3.S01DryKnowledge.Step4;

/// <summary>
/// Krok 4: jedyna, autorytatywna reprezentacja wiedzy "ile kosztuje bilet".
/// Właściciel: dział cennika. Sprzedaż i zwrot tylko z niej korzystają.
/// </summary>
public sealed class TicketPrice
{
    private const decimal MorningDiscount = 5.00m;

    public decimal Of(Ticket ticket)
    {
        var @base = BasePrice(ticket.Format);
        var price = @base - Math.Round(@base * DiscountPercent(ticket.Type) / 100, 2, MidpointRounding.AwayFromZero);
        if (ticket.Start.Hour < 12)
        {
            price -= MorningDiscount;
        }
        return Math.Round(price, 2, MidpointRounding.AwayFromZero);
    }

    private static decimal BasePrice(string format)
    {
        return format switch
        {
            "IMAX" => 40.00m,
            "3D" => 32.00m,
            _ => 25.00m,
        };
    }

    private static int DiscountPercent(string type)
    {
        return type switch
        {
            "STUDENT" => 25,
            "SENIOR" => 30,
            "CHILD" => 40,
            _ => 0,
        };
    }
}
