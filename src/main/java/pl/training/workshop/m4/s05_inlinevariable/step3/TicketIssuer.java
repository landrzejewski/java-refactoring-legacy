package pl.training.workshop.m4.s05_inlinevariable.step3;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import pl.training.workshop.m4.s05_inlinevariable.Ticket;

/**
 * Krok 3 (rozwiązanie): najpierw Rename {@code money(int)} -> {@code moneyFromGrosze}, dopiero potem
 * Inline Variable {@code price}. Bez Rename wklejone {@code money(basePrice(format))} wybrałoby
 * przeciążenie int (grosze) i cena 40 zł stałaby się "0.40". number i issuedAt zostają - celowo.
 */
public final class TicketIssuer {
    private static final Duration HOLD = Duration.ofMinutes(15);
    private static final int ONLINE_FEE_GROSZE = 200;

    private final Clock clock;
    private int lastNumber;

    public TicketIssuer(Clock clock) {
        this.clock = clock;
    }

    public Ticket issue(String screeningCode, int format) {
        int number = nextNumber();
        Instant issuedAt = clock.instant();
        String code = screeningCode + "-" + number;
        return new Ticket(code,
                "Bilet " + code + ", cena " + money(basePrice(format))
                        + ", oplata " + moneyFromGrosze(ONLINE_FEE_GROSZE),
                issuedAt, issuedAt.plus(HOLD));
    }

    private int nextNumber() {
        lastNumber++;
        return lastNumber;
    }

    private static int basePrice(int format) {
        return switch (format) {
            case 3 -> 40;
            case 2 -> 32;
            default -> 25;
        };
    }

    /** Kwota w złotych. */
    private static String money(double zloty) {
        return String.format(Locale.ROOT, "%.2f", zloty);
    }

    /** Kwota w groszach - osobna nazwa, osobna jednostka. */
    private static String moneyFromGrosze(int grosze) {
        return String.format(Locale.ROOT, "%d.%02d", grosze / 100, grosze % 100);
    }
}
