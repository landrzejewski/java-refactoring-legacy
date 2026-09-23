package pl.training.workshop.m3.s10_isp.start;

/** Klient: kasa. Używa sellTicket i refundTicket, a zależy od całego zaplecza. */
public final class CashDesk {
    private final CinemaAdminService backOffice;

    public CashDesk(CinemaAdminService backOffice) {
        this.backOffice = backOffice;
    }

    public String sell(String title, int seat) {
        return "bilet " + backOffice.sellTicket(title, seat) + ": " + title + ", miejsce " + seat;
    }

    public String refund(String ticketId) {
        return backOffice.refundTicket(ticketId);
    }
}
