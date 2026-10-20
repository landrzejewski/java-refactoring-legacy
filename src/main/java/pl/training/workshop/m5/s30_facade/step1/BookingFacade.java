package pl.training.workshop.m5.s30_facade.step1;

import pl.training.workshop.m5.s30_facade.TicketOrder;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: Facade - orkiestracja zakupu wyjęta z OnlineShop (Extract Method + Move Method).
 * Kolejność kroków (cena, rezerwacja, płatność z kompensacją, mail) jest teraz w jednym
 * miejscu; to, co różni kanały (opłata rezerwacyjna), przychodzi jako parametr.
 */
public final class BookingFacade {
    private final SeatRegistry seats;
    private final PriceList prices;
    private final PaymentClient payments;
    private final TicketMailer mailer;

    public BookingFacade(SeatRegistry seats, PriceList prices, PaymentClient payments, TicketMailer mailer) {
        this.seats = seats;
        this.prices = prices;
        this.payments = payments;
        this.mailer = mailer;
    }

    public String book(TicketOrder order, Money feePerSeat) {
        Money total = prices.price(order.ticketType()).plus(feePerSeat).times(order.seats().size());
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
