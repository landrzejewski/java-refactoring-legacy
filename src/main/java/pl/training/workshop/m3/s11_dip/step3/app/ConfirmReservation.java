package pl.training.workshop.m3.s11_dip.step3.app;

import pl.training.workshop.m3.s11_dip.Reservation;

/**
 * Krok 3 (rozwiązanie): Extract Interface z potrzeby + Move Method do adaptera.
 * Polityka nie importuje niczego z infra. Sterowanie nadal płynie app -&gt; infra
 * (confirm woła notifier), ale zależność źródłowa odwróciła się: infra -&gt; app.
 */
public final class ConfirmReservation {
    private final CustomerNotifier notifier;

    public ConfirmReservation(CustomerNotifier notifier) {
        this.notifier = notifier;
    }

    public String confirm(Reservation reservation) {
        if (reservation.seats() < 1) {
            throw new IllegalArgumentException("rezerwacja bez miejsc");
        }
        String message = "Rezerwacja: " + reservation.title() + ", " + reservation.start()
                + ", miejsc: " + reservation.seats() + ". Zaplac w ciagu 15 minut.";
        notifier.notifyCustomer(reservation.email(), message);
        return "potwierdzono: " + reservation.email();
    }
}
