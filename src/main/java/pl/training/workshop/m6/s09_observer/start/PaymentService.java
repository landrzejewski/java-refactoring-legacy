package pl.training.workshop.m6.s09_observer.start;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.m6.s09_observer.LoyaltyProgram;
import pl.training.workshop.m6.s09_observer.Mailer;
import pl.training.workshop.m6.s09_observer.Payment;
import pl.training.workshop.m6.s09_observer.SmsGateway;

/**
 * Start: po opłaceniu serwis na sztywno woła mail, SMS i program lojalnościowy. Każdy nowy
 * odbiorca to zmiana w tej klasie. Semantyka do zachowania: kolejność i fail-fast
 * (wyjątek w SMS przerywa - punkty nie zostaną naliczone, a rezerwacja jest już opłacona).
 */
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
        mailer.send(payment.email(),
                "Potwierdzenie platnosci " + payment.reservationId() + ": " + payment.amount());
        sms.send(payment.phone(), "Oplacono " + payment.reservationId());
        loyalty.addPoints(payment.email(), payment.amount().amount().intValue() / 10);
    }

    public List<String> paid() {
        return List.copyOf(paid);
    }
}
