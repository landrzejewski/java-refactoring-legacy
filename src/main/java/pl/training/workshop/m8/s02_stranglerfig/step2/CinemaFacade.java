package pl.training.workshop.m8.s02_stranglerfig.step2;

import java.util.Map;

import pl.training.workshop.m8.s02_stranglerfig.BookingLedger;
import pl.training.workshop.m8.s02_stranglerfig.CinemaApi;

/**
 * Krok 2: przejęcie pierwszej ścieżki - rezerwacje obsługuje nowy BookingModule,
 * raport nadal legacy. Oba czytają i piszą tę samą bazę, więc raport widzi nowe rezerwacje.
 */
public final class CinemaFacade implements CinemaApi {
    private final LegacyCinema legacy;
    private final BookingModule bookings;

    public CinemaFacade(BookingLedger ledger) {
        this.legacy = new LegacyCinema(ledger);
        this.bookings = new BookingModule(ledger);
    }

    @Override
    public String book(String email, String title, int format, int tickets, boolean web) {
        return bookings.book(email, title, format, tickets, web);
    }

    @Override
    public String report() {
        return legacy.report();
    }

    /** Żywa dokumentacja routingu: kto obsługuje którą operację. */
    public Map<String, String> routes() {
        return Map.of("book", "new", "report", "legacy");
    }
}
