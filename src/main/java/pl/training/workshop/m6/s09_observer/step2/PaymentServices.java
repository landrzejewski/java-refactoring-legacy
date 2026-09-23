package pl.training.workshop.m6.s09_observer.step2;

import pl.training.workshop.m6.s09_observer.LoyaltyProgram;
import pl.training.workshop.m6.s09_observer.Mailer;
import pl.training.workshop.m6.s09_observer.SmsGateway;

/** Krok 2: bez zmian - korzeń kompozycji. */
public final class PaymentServices {
    private PaymentServices() {
    }

    public static PaymentService standard(Mailer mailer, SmsGateway sms, LoyaltyProgram loyalty) {
        return new PaymentService(mailer, sms, loyalty);
    }
}
