package pl.training.workshop.exercises.e02_hallmap.step3;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: miejsce to tylko etykieta (stan własny) i odwołanie do współdzielonej kategorii
 * (stan wspólny). 400 foteli VIP wskazuje na jeden obiekt kategorii.
 */
public final class Seat implements HallElement {
    private final String label;
    private final SeatCategory category;

    Seat(String label, SeatCategory category) {
        this.label = label;
        this.category = category;
    }

    @Override
    public int capacity() {
        return 1;
    }

    @Override
    public Money surcharges() {
        return category.surcharge();
    }

    public String label() {
        return label;
    }

    public SeatCategory category() {
        return category;
    }
}
