package pl.training.workshop.m7.s15_behaviourvector.step3;

import java.util.Objects;

import pl.training.workshop.m7.s15_behaviourvector.Booking;
import pl.training.workshop.m7.s15_behaviourvector.BookingStatus;

/**
 * Krok 3 (rozwiązanie): drugi seam - PaymentGateway. Test zapisuje maile i obciążenia
 * do JEDNEGO dziennika, więc widzi też ich kolejność. Razem ze stanem rezerwacji
 * i wyjątkami to pełny wektor obserwowalnego zachowania tej metody.
 */
public final class TicketCheckout {
    private final Mailer mailer;
    private final PaymentGateway gateway;

    public TicketCheckout() {
        this(CinemaMailer::send, CardTerminal::charge);
    }

    public TicketCheckout(Mailer mailer, PaymentGateway gateway) {
        this.mailer = Objects.requireNonNull(mailer, "mailer");
        this.gateway = Objects.requireNonNull(gateway, "gateway");
    }

    public String pay(Booking booking, String card) {
        Objects.requireNonNull(card, "card");
        if (booking.status() != BookingStatus.NEW) {
            return "ERROR: status " + booking.status();
        }
        if (gateway.charge(card, booking.amount())) {
            booking.markPaid();
            mailer.send(booking.email(), "Bilety " + booking.id() + " oplacone: " + booking.amount());
            return "OK";
        }
        mailer.send(booking.email(), "Platnosc odrzucona " + booking.id());
        return "DECLINED";
    }
}
