package pl.training.workshop.m8.s04_shadowlimits.step1;

import pl.training.workshop.m8.s04_shadowlimits.BookingRequest;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: Parameterize Constructor - efekty przez port Effects zamiast Infrastructure.
 * Refaktoryzacja przygotowawcza: z prawdziwym adapterem zachowanie (także błędne) jest takie samo.
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
