package pl.training.workshop.m3.s11_dip.step1.app;

import pl.training.workshop.m3.s11_dip.Reservation;
import pl.training.workshop.m3.s11_dip.step1.infra.SmtpMailSender;

/**
 * Krok 1: Introduce Parameter - klient SMTP wstrzyknięty przez konstruktor.
 * To jest dependency injection, ale jeszcze NIE DIP: import wciąż prowadzi
 * z polityki do szczegółu (app -&gt; infra), a polityka zna MIME i kody SMTP.
 */
public final class ConfirmReservation {
    private final SmtpMailSender mail;

    public ConfirmReservation(SmtpMailSender mail) {
        this.mail = mail;
    }

    public String confirm(Reservation reservation) {
        if (reservation.seats() < 1) {
            throw new IllegalArgumentException("rezerwacja bez miejsc");
        }
        String message = "Rezerwacja: " + reservation.title() + ", " + reservation.start()
                + ", miejsc: " + reservation.seats() + ". Zaplac w ciagu 15 minut.";
        String mime = "To: " + reservation.email() + "\r\nSubject: Rezerwacja\r\n\r\n" + message;
        String reply = mail.send(reservation.email(), mime);
        if (!reply.startsWith("250")) {
            throw new IllegalStateException("SMTP odrzucil: " + reply);
        }
        return "potwierdzono: " + reservation.email();
    }
}
