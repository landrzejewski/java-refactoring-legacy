package pl.training.workshop.m6.s03_typecode.step3;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: typ domenowy bez wiedzy o kodach trwałych - mapowanie int przeniesione
 * do mappera FormatCodes (granica trwałości).
 */
public enum Format {
    TWO_D("2D", "25.00", false),
    THREE_D("3D", "32.00", true),
    IMAX("IMAX", "40.00", false);

    private final String label;
    private final Money basePrice;
    private final boolean requiresGlasses;

    Format(String label, String basePrice, boolean requiresGlasses) {
        this.label = label;
        this.basePrice = Money.of(basePrice);
        this.requiresGlasses = requiresGlasses;
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
}
