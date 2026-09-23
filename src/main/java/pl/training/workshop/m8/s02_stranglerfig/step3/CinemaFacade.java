package pl.training.workshop.m8.s02_stranglerfig.step3;

import java.util.Map;

import pl.training.workshop.m8.s02_stranglerfig.BookingLedger;
import pl.training.workshop.m8.s02_stranglerfig.CinemaApi;

/**
 * Krok 3: przejęcie kolejnej ścieżki - raport obsługuje ReportModule. LegacyCinema
 * nie ma już żadnego ruchu, ale nadal jest w kodzie (okno wycofania).
 */
public final class CinemaFacade implements CinemaApi {
    private final BookingModule bookings;
    private final ReportModule reports;

    public CinemaFacade(BookingLedger ledger) {
        this.bookings = new BookingModule(ledger);
        this.reports = new ReportModule(ledger);
    }

    @Override
    public String book(String email, String title, int format, int tickets, boolean web) {
        return bookings.book(email, title, format, tickets, web);
    }

    @Override
    public String report() {
        return reports.report();
    }

    /** Żywa dokumentacja routingu: kto obsługuje którą operację. */
    public Map<String, String> routes() {
        return Map.of("book", "new", "report", "new");
    }
}
