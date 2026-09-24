namespace Training.Workshop.M3.S10Isp.Step1;

/// <summary>Krok 1: rola z perspektywy kasy.</summary>
public interface ITicketSales
{
    string SellTicket(string title, int seat);

    string RefundTicket(string ticketId);
}
