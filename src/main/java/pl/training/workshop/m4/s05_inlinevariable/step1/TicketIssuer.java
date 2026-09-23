package pl.training.workshop.m4.s05_inlinevariable.step1;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import pl.training.workshop.m4.s05_inlinevariable.Ticket;

/**
 * Krok 1: Inline Variable {@code label} - czyste wyrażenie użyte raz; nazwa nic nie dodaje,
 * bo parametr rekordu Ticket i tak nazywa się label.
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
        double price = basePrice(format);
        Instant holdUntil = issuedAt.plus(HOLD);
        return new Ticket(code,
                "Bilet " + code + ", cena " + money(price)
                        + ", oplata " + money(ONLINE_FEE_GROSZE),
                issuedAt, holdUntil);
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

    /** Kwota w groszach - ta sama nazwa, inna jednostka. */
    private static String money(int grosze) {
        return String.format(Locale.ROOT, "%d.%02d", grosze / 100, grosze % 100);
    }
}
