package pl.training.workshop.m8.s04_shadowlimits.step2;

import pl.training.workshop.m8.s04_shadowlimits.BookingRequest;
import pl.training.workshop.shared.Money;

/**
 * Krok 2 (bez zmian): nowa ścieżka zależy od portu Effects.
 * W cieniu dostaje nagrywarkę, w produkcji prawdziwy adapter.
 */
public final class NewBookingFlow {
    private static final Money TICKET_2D = Money.of("25.00");
    private static final Money ONLINE_FEE = Money.of("2.00");

    private final Effects effects;

    public NewBookingFlow(Effects effects) {
        this.effects = effects;
    }

    public String book(BookingRequest request) {
        Money tickets = TICKET_2D.times(request.tickets());
        Money total = tickets.plus(ONLINE_FEE.times(request.tickets()));
        effects.charge(request.card(), total);
        effects.save(request.title() + ";" + request.email() + ";" + request.tickets() + ";" + total);
        effects.sendMail(request.email(),
                "Bilety " + request.title() + " x" + request.tickets() + ", zaplacono " + tickets);
        return "OK " + total;
    }
}
