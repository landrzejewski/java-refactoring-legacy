namespace Training.Workshop.M4.S06InlineMethod.Step1;

/// <summary>
/// Krok 1: Inline Method <c>Base</c> - prywatny, niepolimorficzny delegat z jednym wywołaniem.
/// Nie dodawał znaczenia ponad BasePrice, więc to najbezpieczniejszy możliwy Inline.
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
        decimal price = BasePrice(ticket.Format);
        return ticket.Start < new TimeOnly(12, 0) ? price - MorningReduction : price;
    }

    /// <summary>Kasa nie pobiera opłaty rezerwacyjnej. Nadpisywane w OnlineTicketPricing.</summary>
    protected virtual decimal BookingFee()
    {
        return 0.00m;
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
