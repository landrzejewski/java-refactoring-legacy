package pl.training.workshop.m5.s30_facade.step2;

/** Stabilny punkt wejścia sceny (korzeń kompozycji). Krok 2: oba kanały dostają tylko fasadę. */
public final class Cinema {
    private final SeatRegistry seats = new SeatRegistry();
    private final PriceList prices = new PriceList();
    private final PaymentClient payments = new PaymentClient();
    private final TicketMailer mailer = new TicketMailer();
    private final BookingFacade booking = new BookingFacade(seats, prices, payments, mailer);
    private final OnlineShop onlineShop = new OnlineShop(booking);
    private final BoxOffice boxOffice = new BoxOffice(booking);

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
