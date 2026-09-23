package pl.training.workshop.m7.s15_behaviourvector.step1;

import java.util.Objects;

import pl.training.workshop.m7.s15_behaviourvector.Booking;
import pl.training.workshop.m7.s15_behaviourvector.BookingStatus;

/**
 * Krok 1: Parameterize Constructor z Mailer - maile stają się obserwowalne.
 * Kod poza tym bez zmian, więc regresja nadal tu jest - ale teraz test ją WIDZI i dokumentuje.
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
        boolean charged = CardTerminal.charge(card, booking.amount());
        if (charged) {
            booking.markPaid();
        } else {
            mailer.send(booking.email(), "Platnosc odrzucona " + booking.id());
        }
        mailer.send(booking.email(), "Bilety " + booking.id() + " oplacone: " + booking.amount());
        return charged ? "OK" : "DECLINED";
    }
}
