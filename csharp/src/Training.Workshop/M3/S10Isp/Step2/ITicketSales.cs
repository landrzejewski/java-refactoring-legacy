namespace Training.Workshop.M3.S10Isp.Step2;

/// <summary>Rola z perspektywy kasy.</summary>
public interface ITicketSales
{
    string SellTicket(string title, int seat);

    string RefundTicket(string ticketId);
}
