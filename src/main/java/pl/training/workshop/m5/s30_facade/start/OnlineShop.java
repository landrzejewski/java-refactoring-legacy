package pl.training.workshop.m5.s30_facade.start;

import pl.training.workshop.m5.s30_facade.TicketOrder;
import pl.training.workshop.shared.Money;

/**
 * Start: sklep internetowy sam orkiestruje cztery podsystemy w ustalonej kolejności:
 * cena, rezerwacja miejsc, płatność (odmowa zwalnia miejsca), mail z biletem.
 * Do tego opłata rezerwacyjna 2.00 za miejsce.
 */
public final class OnlineShop {
    private static final Money BOOKING_FEE = Money.of("2.00");

    private final SeatRegistry seats;
    private final PriceList prices;
    private final PaymentClient payments;
    private final TicketMailer mailer;

    public OnlineShop(SeatRegistry seats, PriceList prices, PaymentClient payments, TicketMailer mailer) {
        this.seats = seats;
        this.prices = prices;
        this.payments = payments;
        this.mailer = mailer;
    }

    public String buy(TicketOrder order) {
        Money total = prices.price(order.ticketType()).plus(BOOKING_FEE).times(order.seats().size());
        try {
            seats.reserve(order.screening(), order.seats());
        } catch (IllegalStateException exception) {
            return "ERROR " + exception.getMessage();
        }
        if (!payments.charge(order.card(), total)) {
            seats.release(order.screening(), order.seats());
            return "ERROR payment declined";
        }
        mailer.send(order.email(), "Bilety " + order.screening() + " " + String.join(",", order.seats()) + " = " + total);
        return "OK " + total;
    }
}
