namespace Training.Workshop.M4.S06InlineMethod.Start;

/// <summary>
/// Start: cennik kasy. Dwa pośredniki bez znaczenia (Base, AddFee) i jeden hak
/// nadpisywany w OnlineTicketPricing (BookingFee). Wszystkie trzy "wyglądają" na trywialne.
/// </summary>
public class TicketPricing
{
    private const decimal MorningReduction = 5.00m;

    public decimal Total(Ticket ticket)
    {
        return AddFee(Price(ticket));
    }

    public decimal Price(Ticket ticket)
    {
        decimal price = Base(ticket);
        return ticket.Start < new TimeOnly(12, 0) ? price - MorningReduction : price;
    }

    /// <summary>Kasa nie pobiera opłaty rezerwacyjnej. Nadpisywane w OnlineTicketPricing.</summary>
    protected virtual decimal BookingFee()
    {
        return 0.00m;
    }

    private decimal Base(Ticket ticket)
    {
        return BasePrice(ticket.Format);
    }

    private decimal AddFee(decimal price)
    {
        return price + BookingFee();
    }

    private static decimal BasePrice(int format)
    {
        return format switch
        {
            3 => 40.00m,
            2 => 32.00m,
            _ => 25.00m,
        };
    }
}
