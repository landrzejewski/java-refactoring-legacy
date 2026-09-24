namespace Training.Workshop.M3.S10Isp.Step2;

/// <summary>Kasa zależy tylko od roli ITicketSales.</summary>
public sealed class CashDesk
{
    private readonly ITicketSales _backOffice;

    public CashDesk(ITicketSales backOffice)
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
