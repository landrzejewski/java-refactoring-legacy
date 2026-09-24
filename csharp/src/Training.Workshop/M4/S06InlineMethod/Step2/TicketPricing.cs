namespace Training.Workshop.M4.S06InlineMethod.Step2;

/// <summary>
/// Krok 2 (rozwiązanie): Inline Method <c>AddFee</c>. Wklejamy WYWOŁANIE <c>BookingFee()</c>,
/// więc dynamiczna dyspozycja zostaje i OnlineTicketPricing nadal dolicza 2.00.
/// <c>BookingFee</c> NIE inline'ujemy: wklejenie jego ciała z klasy bazowej zabiłoby nadpisanie.
/// </summary>
public class TicketPricing
{
    private const decimal MorningReduction = 5.00m;

    public decimal Total(Ticket ticket)
    {
        return Price(ticket) + BookingFee();
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
