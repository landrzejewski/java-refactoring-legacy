package pl.training.workshop.m8.s04_shadowlimits.step3;

import pl.training.workshop.m8.s04_shadowlimits.BookingRequest;

/**
 * Krok 3: nowa ścieżka = plan (czysty) + wykonanie planu na porcie efektów.
 * Tylko ta klasa wykonuje efekty i używa jej wyłącznie ścieżka autorytatywna.
 */
public final class NewBookingFlow {
    private final BookingPlanner planner = new BookingPlanner();
    private final Effects effects;

    public NewBookingFlow(Effects effects) {
        this.effects = effects;
    }

    public String book(BookingRequest request) {
        BookingPlan plan = planner.plan(request);
        plan.effects().forEach(effect -> effect.applyTo(effects));
        return plan.result();
    }
}
