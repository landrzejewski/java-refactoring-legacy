package pl.training.workshop.m3.s10_isp.step2;

/** Kasa zależy tylko od roli TicketSales. */
public final class CashDesk {
    private final TicketSales backOffice;

    public CashDesk(TicketSales backOffice) {
        this.backOffice = backOffice;
    }

    public String sell(String title, int seat) {
        return "bilet " + backOffice.sellTicket(title, seat) + ": " + title + ", miejsce " + seat;
    }

    public String refund(String ticketId) {
        return backOffice.refundTicket(ticketId);
    }
}
