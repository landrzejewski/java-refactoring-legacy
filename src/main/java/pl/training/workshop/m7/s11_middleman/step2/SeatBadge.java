package pl.training.workshop.m7.s11_middleman.step2;

import java.util.NoSuchElementException;
import java.util.Objects;

import pl.training.workshop.m7.s11_middleman.ScreeningCatalog;

/** Krok 2: Remove Middle Man dla pierwszego klienta - plakietka rozmawia bezpośrednio z katalogiem. */
public final class SeatBadge {
    private final ScreeningCatalog catalog;

    public SeatBadge(ScreeningCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    public String badge(String id) {
        int free = freeSeats(id);
        if (free == 0) {
            return id + ": WYPRZEDANE";
        }
        return catalog.title(id) + " (" + catalog.format(id) + "): " + free + " wolnych";
    }

    private int freeSeats(String id) {
        try {
            return catalog.freeSeats(id);
        } catch (NoSuchElementException e) {
            return 0;
        }
    }
}
