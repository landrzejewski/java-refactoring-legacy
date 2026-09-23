package pl.training.workshop.m7.s10_booleanparameter.step2;

import java.util.Objects;

/**
 * Klient 2 (jeszcze niezmigrowany): kompilator ostrzega o użyciu przestarzałego book().
 * customerHasGlasses to dana z formularza, nie flaga sterująca -
 * nie każdy boolean jest zapachem.
 */
public final class BoxOfficeTerminal {
    private final TicketService tickets;

    public BoxOfficeTerminal(TicketService tickets) {
        this.tickets = Objects.requireNonNull(tickets, "tickets");
    }

    public String sell(String title, String format, int seats, boolean customerHasGlasses) {
        return tickets.book(title, format, seats, false, customerHasGlasses);
    }
}
