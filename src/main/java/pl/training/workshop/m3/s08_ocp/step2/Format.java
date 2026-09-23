package pl.training.workshop.m3.s08_ocp.step2;

import java.math.BigDecimal;

/**
 * Krok 2: wiedza o formacie przeniesiona do enuma (Move Method / Replace Conditional
 * with Polymorphism w wersji "dane zamiast gałęzi"). Nowy format = jedna linia tutaj.
 */
public enum Format {
    TWO_D("2D", new BigDecimal("25.00"), false, "2D"),
    THREE_D("3D", new BigDecimal("32.00"), true, "3D - okulary"),
    IMAX("IMAX", new BigDecimal("40.00"), false, "IMAX - ekran laserowy");

    private final String code;
    private final BigDecimal basePrice;
    private final boolean needsGlasses;
    private final String label;

    Format(String code, BigDecimal basePrice, boolean needsGlasses, String label) {
        this.code = code;
        this.basePrice = basePrice;
        this.needsGlasses = needsGlasses;
        this.label = label;
    }

    public static Format parse(String code) {
        for (Format format : values()) {
            if (format.code.equals(code)) {
                return format;
            }
        }
        throw new IllegalArgumentException("nieznany format: " + code);
    }

    public BigDecimal basePrice() {
        return basePrice;
    }

    public boolean needsGlasses() {
        return needsGlasses;
    }

    public String label() {
        return label;
    }
}
