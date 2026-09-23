package pl.training.workshop.m7.s10_booleanparameter.step4;

import java.util.Objects;

/**
 * Klient zmigrowany w kroku 3 - wywołanie mówi "online, okulary z kina"
 * bez zaglądania do sygnatury.
 */
public final class MobileApp {
    private final TicketService tickets;

    public MobileApp(TicketService tickets) {
        this.tickets = Objects.requireNonNull(tickets, "tickets");
    }

    public String buy(String title, String format, int seats) {
        return tickets.bookOnline(title, format, seats, Glasses.RENTED);
    }
}
