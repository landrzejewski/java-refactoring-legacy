package pl.training.workshop.m6.s09_observer.step3;

import pl.training.workshop.m6.s09_observer.LoyaltyProgram;
import pl.training.workshop.m6.s09_observer.Mailer;
import pl.training.workshop.m6.s09_observer.SmsGateway;

/** Krok 3: korzeń kompozycji - tu (i tylko tu) ustalamy zestaw i kolejność odbiorców. */
public final class PaymentServices {
    private PaymentServices() {
    }

    public static PaymentService standard(Mailer mailer, SmsGateway sms, LoyaltyProgram loyalty) {
        PaymentService service = new PaymentService();
        service.subscribe(new MailConfirmation(mailer));
        service.subscribe(new SmsConfirmation(sms));
        service.subscribe(new LoyaltyPoints(loyalty));
        return service;
    }
}
