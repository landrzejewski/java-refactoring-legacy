package pl.training.workshop.m3.s03_falseabstraction.step1;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Krok 1: Inline Method (wszystkie wywołania, usuń Pricing). Kod wspólnej metody
 * wrócił do wywołującego razem z flagami - tymczasowo nieładnie, ale bezpiecznie.
 */
public final class TicketCounter {
    public BigDecimal ticket(String format, boolean morning, boolean ownGlasses) {
        boolean pass = false;
        BigDecimal unit;
        if (pass) {
            unit = new BigDecimal("20.00");
        } else {
            unit = switch (format) {
                case "IMAX" -> new BigDecimal("40.00");
                case "3D" -> new BigDecimal("32.00");
                default -> new BigDecimal("25.00");
            };
            if (morning) {
                unit = unit.subtract(new BigDecimal("5.00"));
            }
        }
        if (format.equals("3D") && !ownGlasses && !pass) {
            unit = unit.add(new BigDecimal("3.00"));
        }
        return unit.multiply(BigDecimal.valueOf(1)).setScale(2, RoundingMode.HALF_UP);
    }
}
