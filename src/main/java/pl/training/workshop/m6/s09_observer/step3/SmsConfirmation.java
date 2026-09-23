package pl.training.workshop.m6.s09_observer.step3;

import pl.training.workshop.m6.s09_observer.SmsGateway;

/** Krok 3: obserwator - potwierdzenie SMS. */
public record SmsConfirmation(SmsGateway sms) implements PaymentListener {
    @Override
    public void onPaid(ReservationPaid event) {
        sms.send(event.phone(), "Oplacono " + event.reservationId());
    }
}
