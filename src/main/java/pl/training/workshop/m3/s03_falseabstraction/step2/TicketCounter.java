package pl.training.workshop.m3.s03_falseabstraction.step2;

import java.math.BigDecimal;

/**
 * Krok 2 (rozwiązanie): Inline Variable dla stałych flag i Simplify - martwe gałęzie
 * karnetu znikają. Zostaje czysta reguła biletu: format, poranek, okulary 3D.
 */
public final class TicketCounter {
    private static final BigDecimal MORNING_DISCOUNT = new BigDecimal("5.00");
    private static final BigDecimal GLASSES_3D = new BigDecimal("3.00");

    public BigDecimal ticket(String format, boolean morning, boolean ownGlasses) {
        BigDecimal price = switch (format) {
            case "IMAX" -> new BigDecimal("40.00");
            case "3D" -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
        if (morning) {
            price = price.subtract(MORNING_DISCOUNT);
        }
        if (format.equals("3D") && !ownGlasses) {
            price = price.add(GLASSES_3D);
        }
        return price;
    }
}
