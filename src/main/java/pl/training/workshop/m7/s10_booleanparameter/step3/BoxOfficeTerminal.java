package pl.training.workshop.m7.s10_booleanparameter.step3;

import java.util.Objects;

/**
 * Krok 3: klient zmigrowany. customerHasGlasses zostaje booleanem - to dana z formularza,
 * tłumaczymy ją na Glasses na granicy.
 */
public final class BoxOfficeTerminal {
    private final TicketService tickets;

    public BoxOfficeTerminal(TicketService tickets) {
        this.tickets = Objects.requireNonNull(tickets, "tickets");
    }

    public String sell(String title, String format, int seats, boolean customerHasGlasses) {
        Glasses glasses = customerHasGlasses ? Glasses.OWN : Glasses.RENTED;
        return tickets.bookAtBoxOffice(title, format, seats, glasses);
    }
}
