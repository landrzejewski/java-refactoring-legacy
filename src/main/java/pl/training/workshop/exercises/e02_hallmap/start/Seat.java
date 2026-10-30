package pl.training.workshop.exercises.e02_hallmap.start;

import pl.training.workshop.shared.Money;

/**
 * Start: miejsce niesie własną kopię wszystkiego, co opisuje jego kategorię. Dwa fotele VIP
 * w tym samym rzędzie mają dwie osobne kopie nazwy, dopłaty, ikony i opisu.
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

    String label() {
        return label;
    }

    char code() {
        return code;
    }

    String categoryName() {
        return categoryName;
    }

    @Override
    public int capacity() {
        return 1;
    }

    @Override
    public Money surcharges() {
        return surcharge;
    }

    String icon() {
        return icon;
    }

    String description() {
        return description;
    }
}
