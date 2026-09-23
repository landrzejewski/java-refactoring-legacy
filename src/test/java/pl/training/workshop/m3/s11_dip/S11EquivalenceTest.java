package pl.training.workshop.m3.s11_dip;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Potwierdzenie działa tak samo; od kroku 1 sprawdzamy też, co faktycznie poszło przez SMTP. */
final class S11EquivalenceTest {
    private static final Reservation DUNE =
            new Reservation("anna@kino.pl", "Diuna", LocalDateTime.of(2026, 10, 2, 20, 0), 2);
    private static final Reservation BAD_EMAIL =
            new Reservation("jan-kino.pl", "Amator", LocalDateTime.of(2026, 10, 3, 18, 30), 1);
    private static final Reservation NO_SEATS =
            new Reservation("jan@kino.pl", "Amator", LocalDateTime.of(2026, 10, 3, 18, 30), 0);

    private static Function<Reservation, String> safe(Function<Reservation, String> confirm) {
        return r -> {
            try {
                return confirm.apply(r);
            } catch (RuntimeException e) {
                return "blad: " + e.getMessage();
            }
        };
    }

    @TestFactory
    Stream<DynamicTest> everyStepConfirmsTheSame() {
        return Scene.<Reservation, String>variants()
                .variant("start", safe(pl.training.workshop.m3.s11_dip.start.Main.confirmReservation()::confirm))
                .variant("step1", safe(pl.training.workshop.m3.s11_dip.step1.Main.confirmReservation()::confirm))
                .variant("step2", safe(pl.training.workshop.m3.s11_dip.step2.Main.confirmReservation()::confirm))
                .variant("step3", safe(pl.training.workshop.m3.s11_dip.step3.Main.confirmReservation()::confirm))
                .expect("poprawna rezerwacja", DUNE, "potwierdzono: anna@kino.pl")
                .expect("serwer odrzuca adres", BAD_EMAIL, "blad: SMTP odrzucil: 550 mailbox unavailable")
                .expect("rezerwacja bez miejsc", NO_SEATS, "blad: rezerwacja bez miejsc")
                .tests();
    }

    @TestFactory
    Stream<DynamicTest> stepsSendTheSameMimeMessage() {
        return Scene.<Reservation, String>variants()
                .variant("step1", r -> {
                    var smtp = new pl.training.workshop.m3.s11_dip.step1.infra.SmtpMailSender("smtp.kino.pl", 25);
                    new pl.training.workshop.m3.s11_dip.step1.app.ConfirmReservation(smtp).confirm(r);
                    return String.join("|", smtp.transcript());
                })
                .variant("step2", r -> {
                    var smtp = new pl.training.workshop.m3.s11_dip.step2.infra.SmtpMailSender("smtp.kino.pl", 25);
                    new pl.training.workshop.m3.s11_dip.step2.app.ConfirmReservation(smtp).confirm(r);
                    return String.join("|", smtp.transcript());
                })
                .variant("step3", r -> {
                    var smtp = new pl.training.workshop.m3.s11_dip.step3.infra.SmtpMailSender("smtp.kino.pl", 25);
                    new pl.training.workshop.m3.s11_dip.step3.app.ConfirmReservation(
                            new pl.training.workshop.m3.s11_dip.step3.infra.SmtpCustomerNotifier(smtp)).confirm(r);
                    return String.join("|", smtp.transcript());
                })
                .expect("MIME dla Diuny", DUNE, "smtp.kino.pl:25 To: anna@kino.pl\r\nSubject: Rezerwacja\r\n\r\n"
                        + "Rezerwacja: Diuna, 2026-10-02T20:00, miejsc: 2. Zaplac w ciagu 15 minut.")
                .tests();
    }
}
