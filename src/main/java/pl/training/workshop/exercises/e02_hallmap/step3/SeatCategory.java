package pl.training.workshop.exercises.e02_hallmap.step3;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: kategoria miejsca - stan wspólny wielu miejsc, niemutowalny, więc bezpieczny do
 * współdzielenia. Nie wie, w którym rzędzie ani pod jakim numerem stoi fotel.
 */
public final class SeatCategory {
    private final char code;
    private final String name;
    private final Money surcharge;
    private final String icon;
    private final String description;

    SeatCategory(char code, String name, Money surcharge, String icon, String description) {
        this.code = code;
        this.name = name;
        this.surcharge = surcharge;
        this.icon = icon;
        this.description = description;
    }

    public char code() {
        return code;
    }

    public String name() {
        return name;
    }

    public Money surcharge() {
        return surcharge;
    }

    public String icon() {
        return icon;
    }

    public String description() {
        return description;
    }
}
