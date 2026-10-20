package pl.training.workshop.m5.s32_mediator.step1;

/**
 * Krok 1: Mediator - komponent zgłasza, CO się stało, a mediator decyduje, KTO ma zareagować.
 * Na razie jedno zdarzenie: kliknięcie miejsca na mapie.
 */
public interface SelectionMediator {
    void seatToggled(String seat, boolean selected);
}
