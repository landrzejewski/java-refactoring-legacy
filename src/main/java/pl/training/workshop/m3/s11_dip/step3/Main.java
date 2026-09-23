package pl.training.workshop.m3.s11_dip.step3;

import pl.training.workshop.m3.s11_dip.step3.app.ConfirmReservation;
import pl.training.workshop.m3.s11_dip.step3.infra.SmtpCustomerNotifier;
import pl.training.workshop.m3.s11_dip.step3.infra.SmtpMailSender;

/** Composition root wariantu: jedyne miejsce, które zna adapter i składa graf obiektów. */
public final class Main {
    private Main() {
    }

    public static ConfirmReservation confirmReservation() {
        return new ConfirmReservation(new SmtpCustomerNotifier(new SmtpMailSender("smtp.kino.pl", 25)));
    }
}
