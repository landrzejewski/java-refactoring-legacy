package pl.training.workshop.m4.s05_inlinevariable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Duration;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Dokumentuje pułapkę: tak wyglądałby start po naiwnym Inline Variable dla number, issuedAt i price.
 * Kod się kompiluje, IDE nie protestuje - zmienia się liczba i moment ewaluacji oraz przeciążenie.
 */
final class S05InlineTrapTest {
    private final Ticket ticket = new NaivelyInlinedIssuer(new TickingClock(S05EquivalenceTest.T0)).issue("D1", 3);

    @Test
    void inliningASideEffectConsumesTwoNumbers() {
        assertEquals("D1-1", ticket.code());
        assertEquals("Bilet D1-2, cena 0.40, oplata 2.00", ticket.label(), "etykieta ma inny numer niż kod");
    }

    @Test
    void inliningAClockReadGivesTwoDifferentInstants() {
        assertEquals(Duration.ofMinutes(15).plusSeconds(1),
                Duration.between(ticket.issuedAt(), ticket.holdUntil()), "rezerwacja trzyma sekundę za długo");
    }

    @Test
    void inliningADoubleVariablePicksTheIntOverload() {
        assertEquals("0.40", ticket.label().substring(ticket.label().indexOf("cena ") + 5,
                ticket.label().indexOf(", oplata")), "40 zł potraktowane jak 40 groszy");
    }

    /** Kopia start po trzech naiwnych Inline Variable. */
    private static final class NaivelyInlinedIssuer {
        private final Clock clock;
        private int lastNumber;

        NaivelyInlinedIssuer(Clock clock) {
            this.clock = clock;
        }

        Ticket issue(String screeningCode, int format) {
            return new Ticket(screeningCode + "-" + nextNumber(),
                    "Bilet " + screeningCode + "-" + nextNumber() + ", cena " + money(basePrice(format))
                            + ", oplata " + money(200),
                    clock.instant(), clock.instant().plus(Duration.ofMinutes(15)));
        }

        private int nextNumber() {
            lastNumber++;
            return lastNumber;
        }

        private static int basePrice(int format) {
            return format == 3 ? 40 : format == 2 ? 32 : 25;
        }

        @SuppressWarnings("unused")
        private static String money(double zloty) {
            return String.format(Locale.ROOT, "%.2f", zloty);
        }

        private static String money(int grosze) {
            return String.format(Locale.ROOT, "%d.%02d", grosze / 100, grosze % 100);
        }
    }
}
