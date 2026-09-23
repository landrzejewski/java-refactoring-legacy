package pl.training.workshop.m8.s02_stranglerfig.step1;

import java.util.Map;

import pl.training.workshop.m8.s02_stranglerfig.BookingLedger;
import pl.training.workshop.m8.s02_stranglerfig.CinemaApi;

/**
 * Krok 1: fasada 1:1 - każde wywołanie trafia do starego systemu. Zachowanie się nie zmienia,
 * ale od teraz mamy jedno miejsce, w którym można przekierować pojedynczą operację.
 */
public final class CinemaFacade implements CinemaApi {
    private final LegacyCinema legacy;

    public CinemaFacade(BookingLedger ledger) {
        this.legacy = new LegacyCinema(ledger);
    }

    @Override
    public String book(String email, String title, int format, int tickets, boolean web) {
        return legacy.book(email, title, format, tickets, web);
    }

    @Override
    public String report() {
        return legacy.report();
    }

    /** Żywa dokumentacja routingu: kto obsługuje którą operację. */
    public Map<String, String> routes() {
        return Map.of("book", "legacy", "report", "legacy");
    }
}
