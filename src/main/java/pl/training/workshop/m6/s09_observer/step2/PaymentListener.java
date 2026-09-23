package pl.training.workshop.m6.s09_observer.step2;

/** Krok 2: kontrakt obserwatora - subject zna tylko ten interfejs. Wywołanie synchroniczne. */
@FunctionalInterface
public interface PaymentListener {
    void onPaid(ReservationPaid event);
}
