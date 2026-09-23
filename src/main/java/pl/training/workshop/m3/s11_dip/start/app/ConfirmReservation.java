package pl.training.workshop.m3.s11_dip.start.app;

import pl.training.workshop.m3.s11_dip.Reservation;
import pl.training.workshop.m3.s11_dip.start.infra.SmtpMailSender;

/**
 * Start: polityka (potwierdzenie rezerwacji) sama tworzy szczegół techniczny,
 * składa nagłówki MIME i interpretuje kody SMTP. Zależność źródłowa i przepływ
 * sterowania biegną w tę samą stronę: app -&gt; infra. Nie da się jej przetestować
 * bez "wysłania maila" i nie da się zmienić kanału (SMS) bez edycji polityki.
 */
public final class ConfirmReservation {
    private final SmtpMailSender mail = new SmtpMailSender("smtp.kino.pl", 25);

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
