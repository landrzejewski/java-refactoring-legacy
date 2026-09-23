package pl.training.workshop.m3.s08_ocp.step3;

import java.math.BigDecimal;

/**
 * Krok 3 (rozwiązanie): nowy format 4DX (45.00, okulary 3D) to jedna nowa stała.
 * ScreeningOffer nie zmieniła się ani o znak - to jest OCP na wybranej osi.
 */
public enum Format {
    TWO_D("2D", new BigDecimal("25.00"), false, "2D"),
    THREE_D("3D", new BigDecimal("32.00"), true, "3D - okulary"),
    IMAX("IMAX", new BigDecimal("40.00"), false, "IMAX - ekran laserowy"),
    FOUR_DX("4DX", new BigDecimal("45.00"), true, "4DX - ruchome fotele");

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
