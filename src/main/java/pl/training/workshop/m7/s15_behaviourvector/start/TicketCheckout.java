package pl.training.workshop.m7.s15_behaviourvector.start;

import java.util.Objects;

import pl.training.workshop.m7.s15_behaviourvector.Booking;
import pl.training.workshop.m7.s15_behaviourvector.BookingStatus;

/**
 * Start: kod po "porządkach" kolegi (Consolidate Duplicate Conditional Fragments) -
 * mail potwierdzający wysunięto za if, więc dostaje go także klient z odrzuconą kartą.
 * Test sprawdza tylko wynik (OK / DECLINED) i jest zielony. Regresja przeszła.
 */
public final class TicketCheckout {
    public String pay(Booking booking, String card) {
        Objects.requireNonNull(card, "card");
        if (booking.status() != BookingStatus.NEW) {
            return "ERROR: status " + booking.status();
        }
        boolean charged = CardTerminal.charge(card, booking.amount());
        if (charged) {
            booking.markPaid();
        } else {
            CinemaMailer.send(booking.email(), "Platnosc odrzucona " + booking.id());
        }
        CinemaMailer.send(booking.email(), "Bilety " + booking.id() + " oplacone: " + booking.amount());
        return charged ? "OK" : "DECLINED";
    }
}
