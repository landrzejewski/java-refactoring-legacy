package pl.training.workshop.exercises.e02_hallmap.step1;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: miejsce jest liściem drzewa - jego pojemność to 1, a dopłata to jego własna dopłata.
 * Dane kategorii nadal są kopiowane do każdego miejsca (to osobny zapach, krok 3).
 */
public final class Seat implements HallElement {
    private final String label;
    private final char code;
    private final String categoryName;
    private final Money surcharge;
    private final String icon;
    private final String description;

    Seat(String label, char code, String categoryName, Money surcharge, String icon, String description) {
        this.label = label;
        this.code = code;
        this.categoryName = categoryName;
        this.surcharge = surcharge;
        this.icon = icon;
        this.description = description;
    }

    @Override
    public int capacity() {
        return 1;
    }

    @Override
    public Money surcharges() {
        return surcharge;
    }

    String label() {
        return label;
    }

    char code() {
        return code;
    }

    String categoryName() {
        return categoryName;
    }

    Money surcharge() {
        return surcharge;
    }

    String icon() {
        return icon;
    }

    String description() {
        return description;
    }
}
