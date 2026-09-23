package pl.training.workshop.m6.s09_observer.step3;

import pl.training.workshop.m6.s09_observer.Mailer;

/** Krok 3: obserwator - potwierdzenie mailem. */
public record MailConfirmation(Mailer mailer) implements PaymentListener {
    @Override
    public void onPaid(ReservationPaid event) {
        mailer.send(event.email(),
                "Potwierdzenie platnosci " + event.reservationId() + ": " + event.amount());
    }
}
