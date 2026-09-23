package pl.training.workshop.m3.s11_dip.step2.app;

import pl.training.workshop.m3.s11_dip.Reservation;
import pl.training.workshop.m3.s11_dip.step2.infra.SmtpMailSender;

/**
 * Krok 2: Extract Method - potrzeba polityki dostaje nazwę w języku problemu:
 * {@code notifyCustomer(email, message)}. Cały "technologiczny" kod (MIME, kody SMTP)
 * trafił do tej jednej metody. Tak wygląda port, zanim stanie się interfejsem.
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
        notifyCustomer(reservation.email(), message);
        return "potwierdzono: " + reservation.email();
    }

    private void notifyCustomer(String email, String message) {
        String mime = "To: " + email + "\r\nSubject: Rezerwacja\r\n\r\n" + message;
        String reply = mail.send(email, mime);
        if (!reply.startsWith("250")) {
            throw new IllegalStateException("SMTP odrzucil: " + reply);
        }
    }
}
