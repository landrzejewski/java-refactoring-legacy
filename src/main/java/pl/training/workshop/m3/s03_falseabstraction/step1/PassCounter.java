package pl.training.workshop.m3.s03_falseabstraction.step1;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Krok 1: Inline Method - karnet dostał własną kopię, wciąż z cudzymi flagami. */
public final class PassCounter {
    public BigDecimal pass(int entries) {
        String format = "2D";
        boolean pass = true;
        boolean morning = false;
        boolean ownGlasses = true;
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
        return unit.multiply(BigDecimal.valueOf(entries)).setScale(2, RoundingMode.HALF_UP);
    }
}
