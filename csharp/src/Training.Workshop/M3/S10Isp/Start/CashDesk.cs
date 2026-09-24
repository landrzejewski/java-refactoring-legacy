namespace Training.Workshop.M3.S10Isp.Start;

/// <summary>Klient: kasa. Używa SellTicket i RefundTicket, a zależy od całego zaplecza.</summary>
public sealed class CashDesk
{
    private readonly ICinemaAdminService _backOffice;

    public CashDesk(ICinemaAdminService backOffice)
    {
        _backOffice = backOffice;
    }

    public string Sell(string title, int seat)
    {
        return "bilet " + _backOffice.SellTicket(title, seat) + ": " + title + ", miejsce " + seat;
    }

    public string Refund(string ticketId)
    {
        return _backOffice.RefundTicket(ticketId);
    }
}
