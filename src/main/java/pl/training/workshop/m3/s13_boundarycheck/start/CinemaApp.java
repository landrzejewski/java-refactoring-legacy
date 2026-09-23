package pl.training.workshop.m3.s13_boundarycheck.start;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import pl.training.workshop.m3.s13_boundarycheck.start.adapter.ScreeningRow;
import pl.training.workshop.m3.s13_boundarycheck.start.domain.Screening;
import pl.training.workshop.m3.s13_boundarycheck.start.domain.ScreeningService;

/**
 * Punkt startowy wariantu (composition root): składa obiekty i opisuje seans -
 * cena, wiersz bazy, zgodność odczytu. Test woła tylko tę metodę.
 */
public final class CinemaApp {
    private CinemaApp() {
    }

    public static String describe(String title, LocalDateTime start) {
        ScreeningService service = new ScreeningService(
                new BigDecimal("25.00"), new BigDecimal("5.00"), "screenings");
        Screening screening = new Screening(title, start);
        ScreeningRow row = service.toRow(screening);
        return service.price(screening) + " | " + row + " | " + service.fromRow(row).equals(screening);
    }
}
