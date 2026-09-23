package pl.training.workshop.m6.s09_observer.step2;

import pl.training.workshop.m6.s09_observer.LoyaltyProgram;

/** Krok 2: obserwator - 1 punkt za każde pełne 10.00. */
public record LoyaltyPoints(LoyaltyProgram loyalty) implements PaymentListener {
    @Override
    public void onPaid(ReservationPaid event) {
        loyalty.addPoints(event.email(), event.amount().amount().intValue() / 10);
    }
}
