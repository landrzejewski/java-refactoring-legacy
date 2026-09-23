package pl.training.workshop.m3.s13_boundarycheck;

import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.support.Scene;

/** Naprawa granicy nie zmienia ceny ani mapowania na wiersz i z powrotem. */
final class S13EquivalenceTest {
    record Case(String title, LocalDateTime start) {
    }

    private static Function<Case, String> of(java.util.function.BiFunction<String, LocalDateTime, String> app) {
        return c -> app.apply(c.title(), c.start());
    }

    @TestFactory
    Stream<DynamicTest> everyStepPricesAndMapsTheSame() {
        return Scene.<Case, String>variants()
                .variant("start", of(pl.training.workshop.m3.s13_boundarycheck.start.CinemaApp::describe))
                .variant("step1", of(pl.training.workshop.m3.s13_boundarycheck.step1.CinemaApp::describe))
                .variant("step2", of(pl.training.workshop.m3.s13_boundarycheck.step2.CinemaApp::describe))
                .expect("seans wieczorny", new Case("Amator", LocalDateTime.of(2026, 10, 2, 20, 0)),
                        "25.00 | ScreeningRow[table=screenings, title=Amator, start=2026-10-02 20:00:00.0] | true")
                .expect("seans poranny", new Case("Kraina Lodu", LocalDateTime.of(2026, 10, 3, 10, 30)),
                        "20.00 | ScreeningRow[table=screenings, title=Kraina Lodu, start=2026-10-03 10:30:00.0] | true")
                .tests();
    }
}
