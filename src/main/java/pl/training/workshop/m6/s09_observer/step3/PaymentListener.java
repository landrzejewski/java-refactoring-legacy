package pl.training.workshop.m6.s09_observer.step3;

/** Krok 3: kontrakt obserwatora - subject zna tylko ten interfejs. Wywołanie synchroniczne. */
@FunctionalInterface
public interface PaymentListener {
    void onPaid(ReservationPaid event);
}
