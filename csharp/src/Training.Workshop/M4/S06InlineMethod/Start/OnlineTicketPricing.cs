namespace Training.Workshop.M4.S06InlineMethod.Start;

/// <summary>Sprzedaż internetowa: ta sama cena biletu, plus opłata rezerwacyjna 2.00.</summary>
public sealed class OnlineTicketPricing : TicketPricing
{
    protected override decimal BookingFee()
    {
        return 2.00m;
    }
}
