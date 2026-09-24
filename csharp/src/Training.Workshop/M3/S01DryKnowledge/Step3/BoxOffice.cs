namespace Training.Workshop.M3.S01DryKnowledge.Step3;

/// <summary>
/// Krok 3: Substitute Algorithm - zwrot korzysta z <c>TicketPrice</c>,
/// kopia <c>PaidFor</c> usunięta (Safe Delete). Test równoważności potwierdza,
/// że obie kopie reguły dawały te same kwoty dla wszystkich przypadków.
/// </summary>
public sealed class BoxOffice
{
    public decimal Sell(Ticket ticket)
    {
        return TicketPrice(ticket);
    }

    public decimal Refund(Ticket ticket, long hoursBeforeStart)
    {
        var paid = TicketPrice(ticket);
        // zwrot: >= 24h 100%, < 24h 50%, po starcie 0%; potrącenie 3.00
        var percent = hoursBeforeStart >= 24 ? 100 : hoursBeforeStart > 0 ? 50 : 0;
        var refund = Math.Round(paid * percent / 100, 2, MidpointRounding.AwayFromZero) - 3.00m;
        return Math.Round(Math.Max(refund, 0m), 2, MidpointRounding.AwayFromZero);
    }

    private static decimal TicketPrice(Ticket ticket)
    {
        var @base = ticket.Format switch
        {
            "IMAX" => 40.00m,
            "3D" => 32.00m,
            _ => 25.00m,
        };
        var discount = ticket.Type switch
        {
            "STUDENT" => 25,
            "SENIOR" => 30,
            "CHILD" => 40,
            _ => 0,
        };
        var price = @base - Math.Round(@base * discount / 100, 2, MidpointRounding.AwayFromZero);
        if (ticket.Start.Hour < 12)
        {
            price -= 5.00m;
        }
        return Math.Round(price, 2, MidpointRounding.AwayFromZero);
    }
}
