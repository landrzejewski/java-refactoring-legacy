package pl.training.workshop.m6.s09_observer.step1;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m6.s09_observer.LoyaltyProgram;
import pl.training.workshop.m6.s09_observer.Mailer;
import pl.training.workshop.m6.s09_observer.Payment;
import pl.training.workshop.m6.s09_observer.SmsGateway;

/** Krok 1: Extract Method notifyPaid + obiekt zdarzenia. Powiadomienia nadal na sztywno. */
public final class PaymentService {
    private final Mailer mailer;
    private final SmsGateway sms;
    private final LoyaltyProgram loyalty;
    private final List<String> paid = new ArrayList<>();

    public PaymentService(Mailer mailer, SmsGateway sms, LoyaltyProgram loyalty) {
        this.mailer = mailer;
        this.sms = sms;
        this.loyalty = loyalty;
    }

    public void confirm(Payment payment) {
        if (payment.amount().amount().signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        paid.add(payment.reservationId());
        notifyPaid(new ReservationPaid(
                payment.reservationId(), payment.email(), payment.phone(), payment.amount()));
    }

    private void notifyPaid(ReservationPaid event) {
        mailer.send(event.email(),
                "Potwierdzenie platnosci " + event.reservationId() + ": " + event.amount());
        sms.send(event.phone(), "Oplacono " + event.reservationId());
        loyalty.addPoints(event.email(), event.amount().amount().intValue() / 10);
    }

    public List<String> paid() {
        return List.copyOf(paid);
    }
}
