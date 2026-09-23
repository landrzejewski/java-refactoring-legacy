package pl.training.workshop.m3.s03_falseabstraction.start;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Start: fałszywa abstrakcja. Jedna "uniwersalna" metoda wycenia bilety i karnety
 * (karnet: 20.00 za wejście na seans 2D), sterowana flagami boolean. Każdy wywołujący
 * podaje flagi, które go nie dotyczą, a zmiana reguły biletów grozi zmianą karnetów.
 */
public final class Pricing {
    public BigDecimal price(String format, int quantity, boolean pass,
                            boolean morning, boolean ownGlasses) {
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
        return unit.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }
}
