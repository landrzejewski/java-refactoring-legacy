package pl.training.workshop.m5.s30_facade.start;

import pl.training.workshop.m5.s30_facade.TicketOrder;
import pl.training.workshop.shared.Money;

/**
 * Start: kasa biletowa - ta sama orkiestracja co w OnlineShop, skopiowana kilka lat temu
 * i od tamtej pory rozwijana osobno. Kasa nie pobiera opłaty rezerwacyjnej.
 * Porównaj obie metody linia po linii, zanim cokolwiek wydzielisz.
 */
public final class BoxOffice {
    private final SeatRegistry seats;
    private final PriceList prices;
    private final PaymentClient payments;
    private final TicketMailer mailer;

    public BoxOffice(SeatRegistry seats, PriceList prices, PaymentClient payments, TicketMailer mailer) {
        this.seats = seats;
        this.prices = prices;
        this.payments = payments;
        this.mailer = mailer;
    }

    public String sell(TicketOrder order) {
        Money total = prices.price(order.ticketType()).times(order.seats().size());
        try {
            seats.reserve(order.screening(), order.seats());
        } catch (IllegalStateException exception) {
            return "ERROR " + exception.getMessage();
        }
        if (!payments.charge(order.card(), total)) {
            return "ERROR payment declined";
        }
        mailer.send(order.email(), "Bilety " + order.screening() + " " + String.join(",", order.seats()) + " = " + total);
        return "OK " + total;
    }
}
