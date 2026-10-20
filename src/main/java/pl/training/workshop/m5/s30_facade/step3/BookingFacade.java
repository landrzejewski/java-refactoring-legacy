package pl.training.workshop.m5.s30_facade.step3;

import pl.training.workshop.m5.s30_facade.TicketOrder;
import pl.training.workshop.shared.Money;

/**
 * Krok 3: fasada jako jedyne wejście do podsystemu i strażnik spójności. Podsystemy są
 * package-private, a konstruktor fasady też - kanały sprzedaży dostają gotowy obiekt.
 * Niezmiennik "miejsce zajęte tylko po udanej płatności" jest egzekwowany zawsze,
 * także gdy bramka rzuci wyjątkiem (finally). Kod klienta nie może już pominąć kroku,
 * bo nie ma do kroków dostępu.
 */
public final class BookingFacade {
    private final SeatRegistry seats;
    private final PriceList prices;
    private final PaymentClient payments;
    private final TicketMailer mailer;

    BookingFacade(SeatRegistry seats, PriceList prices, PaymentClient payments, TicketMailer mailer) {
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
        boolean paid = false;
        try {
            paid = payments.charge(order.card(), total);
        } finally {
            if (!paid) {
                seats.release(order.screening(), order.seats());
            }
        }
        if (!paid) {
            return "ERROR payment declined";
        }
        mailer.send(order.email(), "Bilety " + order.screening() + " " + String.join(",", order.seats()) + " = " + total);
        return "OK " + total;
    }
}
