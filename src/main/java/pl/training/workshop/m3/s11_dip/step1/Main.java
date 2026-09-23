package pl.training.workshop.m3.s11_dip.step1;

import pl.training.workshop.m3.s11_dip.step1.app.ConfirmReservation;
import pl.training.workshop.m3.s11_dip.step1.infra.SmtpMailSender;

/** Composition root wariantu: jedyne miejsce, które składa graf obiektów. */
public final class Main {
    private Main() {
    }

    public static ConfirmReservation confirmReservation() {
        return new ConfirmReservation(new SmtpMailSender("smtp.kino.pl", 25));
    }
}
