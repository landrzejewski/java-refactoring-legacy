package pl.training.workshop.m7.s10_booleanparameter.step1;

import java.util.Objects;

/** Klient 1 (jeszcze niezmigrowany): kompilator ostrzega o użyciu przestarzałego book(). */
public final class MobileApp {
    private final TicketService tickets;

    public MobileApp(TicketService tickets) {
        this.tickets = Objects.requireNonNull(tickets, "tickets");
    }

    public String buy(String title, String format, int seats) {
        return tickets.book(title, format, seats, true, false);
    }
}
