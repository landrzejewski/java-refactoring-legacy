package pl.training.workshop.m3.s10_isp.step2;

/** Rola z perspektywy kasy. */
public interface TicketSales {
    String sellTicket(String title, int seat);

    String refundTicket(String ticketId);
}
