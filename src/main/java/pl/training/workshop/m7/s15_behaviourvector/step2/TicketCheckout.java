package pl.training.workshop.m7.s15_behaviourvector.step2;

import java.util.Objects;

import pl.training.workshop.m7.s15_behaviourvector.Booking;
import pl.training.workshop.m7.s15_behaviourvector.BookingStatus;

/**
 * Krok 2: naprawa regresji pod ochroną testu z kroku 1 - mail potwierdzający wraca
 * do gałęzi sukcesu. Wynik metody się nie zmienia, zmienia się efekt uboczny.
 */
public final class TicketCheckout {
    private final Mailer mailer;

    public TicketCheckout() {
        this(CinemaMailer::send);
    }

    public TicketCheckout(Mailer mailer) {
        this.mailer = Objects.requireNonNull(mailer, "mailer");
    }

    public String pay(Booking booking, String card) {
        Objects.requireNonNull(card, "card");
        if (booking.status() != BookingStatus.NEW) {
            return "ERROR: status " + booking.status();
        }
        if (CardTerminal.charge(card, booking.amount())) {
            booking.markPaid();
            mailer.send(booking.email(), "Bilety " + booking.id() + " oplacone: " + booking.amount());
            return "OK";
        }
        mailer.send(booking.email(), "Platnosc odrzucona " + booking.id());
        return "DECLINED";
    }
}
