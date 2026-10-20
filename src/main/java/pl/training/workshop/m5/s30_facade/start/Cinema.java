package pl.training.workshop.m5.s30_facade.start;

/** Stabilny punkt wejścia sceny (korzeń kompozycji): składa podsystemy i kanały sprzedaży. */
public final class Cinema {
    private final SeatRegistry seats = new SeatRegistry();
    private final PriceList prices = new PriceList();
    private final PaymentClient payments = new PaymentClient();
    private final TicketMailer mailer = new TicketMailer();
    private final OnlineShop onlineShop = new OnlineShop(seats, prices, payments, mailer);
    private final BoxOffice boxOffice = new BoxOffice(seats, prices, payments, mailer);

    public OnlineShop onlineShop() {
        return onlineShop;
    }

    public BoxOffice boxOffice() {
        return boxOffice;
    }

    /** Obserwowalny stan wszystkich podsystemów naraz. */
    public String state() {
        return "miejsca=" + seats.describe() + " platnosci=" + payments.log() + " maile=" + mailer.sent();
    }
}
