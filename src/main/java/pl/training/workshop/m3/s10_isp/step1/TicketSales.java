package pl.training.workshop.m3.s10_isp.step1;

/** Krok 1: rola z perspektywy kasy. */
public interface TicketSales {
    String sellTicket(String title, int seat);

    String refundTicket(String ticketId);
}
