package pl.training.workshop.m7.s10_booleanparameter.start;

import java.math.BigDecimal;

/**
 * Start: dwie flagi w publicznym API. Wywołanie book("Diuna", "IMAX", 2, true, false)
 * nie mówi, co znaczy true, a co false - trzeba zajrzeć do sygnatury.
 */
public final class TicketService {
    private static final BigDecimal GLASSES = new BigDecimal("3.00");
    private static final BigDecimal FEE = new BigDecimal("2.00");

    public String book(String title, String format, int seats, boolean online, boolean ownGlasses) {
        BigDecimal base = switch (format) {
            case "IMAX" -> new BigDecimal("40.00");
            case "3D" -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
        BigDecimal count = BigDecimal.valueOf(seats);
        BigDecimal total = base.multiply(count);
        if (format.equals("3D") && !ownGlasses) {
            total = total.add(GLASSES.multiply(count));
        }
        if (online) {
            total = total.add(FEE.multiply(count));
        }
        return title + " " + format + " x" + seats + (online ? " online" : " kasa") + ": " + total;
    }
}
