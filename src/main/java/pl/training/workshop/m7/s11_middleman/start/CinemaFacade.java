package pl.training.workshop.m7.s11_middleman.start;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import pl.training.workshop.m7.s11_middleman.Screening;
import pl.training.workshop.m7.s11_middleman.ScreeningCatalog;

/**
 * Start: pośrednik - prawie każda metoda deleguje 1:1 do ScreeningCatalog.
 * Prawie: freeSeats() po cichu tłumaczy "brak seansu" na 0. Zanim usuniesz pośrednika,
 * sprawdź, co naprawdę robi (autoryzacja, logi, transakcje, translacja błędów).
 */
public final class CinemaFacade {
    private final ScreeningCatalog catalog;

    public CinemaFacade(ScreeningCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog, "catalog");
    }

    public String title(String id) {
        return catalog.title(id);
    }

    public String format(String id) {
        return catalog.format(id);
    }

    public int freeSeats(String id) {
        try {
            return catalog.freeSeats(id);
        } catch (NoSuchElementException e) {
            return 0;
        }
    }

    public List<Screening> screenings() {
        return catalog.all();
    }
}
