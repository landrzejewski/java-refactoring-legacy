package pl.training.workshop.m7.s11_middleman.step1;

import java.util.List;
import java.util.Objects;

import pl.training.workshop.m7.s11_middleman.Screening;
import pl.training.workshop.m7.s11_middleman.ScreeningCatalog;

/**
 * Krok 1: pośrednik jest już czystym forwarderem - tłumaczenie "brak seansu" -> 0
 * przeniesione do jedynego klienta, który na nim polega (SeatBadge).
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
