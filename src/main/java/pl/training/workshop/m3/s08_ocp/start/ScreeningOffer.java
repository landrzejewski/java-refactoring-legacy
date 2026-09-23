package pl.training.workshop.m3.s08_ocp.start;

import java.math.BigDecimal;

/**
 * Start: wiedza o formatach seansu rozsiana po kilku switchach na stringu.
 * Każdy nowy format (kino kupuje salę 4DX) wymaga edycji wszystkich switchy,
 * a kompilator nie podpowie, o którym zapomnieliśmy - wpadnie do default.
 */
public final class ScreeningOffer {
    public BigDecimal price(String format, boolean ownGlasses) {
        BigDecimal base = switch (format) {
            case "2D" -> new BigDecimal("25.00");
            case "3D" -> new BigDecimal("32.00");
            case "IMAX" -> new BigDecimal("40.00");
            default -> throw new IllegalArgumentException("nieznany format: " + format);
        };
        BigDecimal glasses = switch (format) {
            case "3D" -> ownGlasses ? BigDecimal.ZERO : new BigDecimal("3.00");
            default -> BigDecimal.ZERO;
        };
        return base.add(glasses);
    }

    public String label(String format) {
        return switch (format) {
            case "2D" -> "2D";
            case "3D" -> "3D - okulary";
            case "IMAX" -> "IMAX - ekran laserowy";
            default -> throw new IllegalArgumentException("nieznany format: " + format);
        };
    }
}
