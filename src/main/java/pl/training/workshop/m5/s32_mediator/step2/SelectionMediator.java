package pl.training.workshop.m5.s32_mediator.step2;

import java.util.List;

/**
 * Krok 2: wszystkie zdarzenia komponentów idą przez mediatora. Uwaga na seatsPaid: przycisk
 * nadal sam zbiera dane (miejsca, kwotę) od kolegów i tylko zgłasza wynik - to zostało na krok 3.
 */
public interface SelectionMediator {
    void seatToggled(String seat, boolean selected);

    void seatRemoved(String seat);

    void basketCleared();

    void voucherApplied();

    void seatsPaid(List<String> seats);
}
