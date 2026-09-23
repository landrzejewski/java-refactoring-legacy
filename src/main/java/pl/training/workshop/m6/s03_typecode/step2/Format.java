package pl.training.workshop.m6.s03_typecode.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: Move Method - zachowanie zależne od formatu przeniesione do typu Format. */
public enum Format {
    TWO_D(1, "2D", "25.00", false),
    THREE_D(2, "3D", "32.00", true),
    IMAX(3, "IMAX", "40.00", false);

    private final int code;
    private final String label;
    private final Money basePrice;
    private final boolean requiresGlasses;

    Format(int code, String label, String basePrice, boolean requiresGlasses) {
        this.code = code;
        this.label = label;
        this.basePrice = Money.of(basePrice);
        this.requiresGlasses = requiresGlasses;
    }

    public int code() {
        return code;
    }

    public String label() {
        return label;
    }

    public Money basePrice() {
        return basePrice;
    }

    public boolean requiresGlasses() {
        return requiresGlasses;
    }

    public static Format fromCode(int code) {
        for (Format format : values()) {
            if (format.code == code) {
                return format;
            }
        }
        throw new IllegalArgumentException("unknown format code: " + code);
    }
}
