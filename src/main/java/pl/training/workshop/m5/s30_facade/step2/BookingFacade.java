package pl.training.workshop.m5.s30_facade.step2;

import pl.training.workshop.m5.s30_facade.TicketOrder;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: fasada obsługuje oba kanały. Kasa dostała w prezencie kompensację, której
 * jej kopia nie miała - odmowa płatności w kasie zwalnia teraz miejsca. To zmiana zachowania
 * (naprawa błędu), udokumentowana osobnym testem, a nie "czysta" refaktoryzacja.
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
