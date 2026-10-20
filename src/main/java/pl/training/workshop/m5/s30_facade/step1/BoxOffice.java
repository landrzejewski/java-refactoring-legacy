package pl.training.workshop.m5.s30_facade.step1;

import pl.training.workshop.m5.s30_facade.TicketOrder;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: bez zmian - kasa nadal orkiestruje podsystemy sama. Zestawienie jej metody
 * z BookingFacade.book pokazuje różnicę: przy odmowie płatności kasa nie zwalnia miejsc.
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
