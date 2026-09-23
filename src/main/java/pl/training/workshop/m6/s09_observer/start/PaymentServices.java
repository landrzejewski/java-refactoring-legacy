package pl.training.workshop.m6.s09_observer.start;

import pl.training.workshop.m6.s09_observer.LoyaltyProgram;
import pl.training.workshop.m6.s09_observer.Mailer;
import pl.training.workshop.m6.s09_observer.SmsGateway;

/** Start: korzeń kompozycji aplikacji - tu powstaje serwis płatności. */
public final class PaymentServices {
    private PaymentServices() {
    }

    public static PaymentService standard(Mailer mailer, SmsGateway sms, LoyaltyProgram loyalty) {
        return new PaymentService(mailer, sms, loyalty);
    }
}
