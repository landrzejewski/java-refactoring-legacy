package pl.training.workshop.m6.s09_observer.step2;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m6.s09_observer.LoyaltyProgram;
import pl.training.workshop.m6.s09_observer.Mailer;
import pl.training.workshop.m6.s09_observer.Payment;
import pl.training.workshop.m6.s09_observer.SmsGateway;

/**
 * Krok 2: Replace Hard-coded Notifications with Observer - odbiorcy jako lista PaymentListener.
 * Konstruktor bez zmian, lista w starej kolejności, pętla bez try/catch (fail-fast jak w start).
 */
public final class PaymentService {
    private final List<PaymentListener> listeners;
    private final List<String> paid = new ArrayList<>();

    public PaymentService(Mailer mailer, SmsGateway sms, LoyaltyProgram loyalty) {
        this.listeners = List.of(
                new MailConfirmation(mailer), new SmsConfirmation(sms), new LoyaltyPoints(loyalty));
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
        for (PaymentListener listener : listeners) {
            listener.onPaid(event);
        }
    }

    public List<String> paid() {
        return List.copyOf(paid);
    }
}
