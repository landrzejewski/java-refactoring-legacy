package pl.training.workshop.m3.s13_boundarycheck.step1;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import pl.training.workshop.m3.s13_boundarycheck.step1.adapter.ScreeningRow;
import pl.training.workshop.m3.s13_boundarycheck.step1.domain.Screening;
import pl.training.workshop.m3.s13_boundarycheck.step1.domain.ScreeningRowMapper;
import pl.training.workshop.m3.s13_boundarycheck.step1.domain.ScreeningService;

/**
 * Punkt startowy wariantu (composition root): składa obiekty i opisuje seans -
 * cena, wiersz bazy, zgodność odczytu. Test woła tylko tę metodę.
 */
public final class CinemaApp {
    private CinemaApp() {
    }

    public static String describe(String title, LocalDateTime start) {
        ScreeningService service = new ScreeningService(new BigDecimal("25.00"), new BigDecimal("5.00"));
        ScreeningRowMapper mapper = new ScreeningRowMapper("screenings");
        Screening screening = new Screening(title, start);
        ScreeningRow row = mapper.toRow(screening);
        return service.price(screening) + " | " + row + " | " + mapper.fromRow(row).equals(screening);
    }
}
