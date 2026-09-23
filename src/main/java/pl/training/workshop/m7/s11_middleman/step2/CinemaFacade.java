package pl.training.workshop.m7.s11_middleman.step2;

import java.util.List;
import java.util.Objects;

import pl.training.workshop.m7.s11_middleman.Screening;
import pl.training.workshop.m7.s11_middleman.ScreeningCatalog;

/**
 * Krok 2: pośrednik (czysty forwarder od kroku 1) został już tylko jednemu klientowi - DailyBoard.
 * Usuniemy go, gdy zmigrujemy ostatniego klienta.
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
        return catalog.freeSeats(id);
    }

    public List<Screening> screenings() {
        return catalog.all();
    }
}
