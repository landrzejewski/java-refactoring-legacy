package pl.training.workshop.exercises.e08_series;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import pl.training.workshop.shared.Money;
import pl.training.workshop.support.Scene;

/**
 * Seria tygodniowa z szablonu: wartości domyślne, własna cena, dubbing, inna sala na koniec,
 * premiera przy jednym tygodniu oraz kolejność błędów walidacji.
 * Scenariusz "premiera przy wielu tygodniach" celowo nie jest tu obecny: start ma w nim błąd
 * płytkiej kopii, który rozwiązanie świadomie naprawia - dokumentuje to {@link E08SolutionTest}.
 */
final class E08EquivalenceTest {
    private static final LocalDateTime FRIDAY_EVENING = LocalDateTime.of(2026, 11, 6, 18, 0);
    private static final LocalDateTime SATURDAY_MORNING = LocalDateTime.of(2026, 11, 7, 10, 30);
    private static final LocalDateTime SUNDAY_EVENING = LocalDateTime.of(2026, 11, 8, 20, 0);

    @TestFactory
    Stream<DynamicTest> everyStepPlansTheSameSeries() {
        return Scene.<SeriesRequest, String>variants()
                .variant("start", r -> run(() -> new pl.training.workshop.exercises.e08_series.start.SeriesPlanner()
                        .plan(r).stream().map(pl.training.workshop.exercises.e08_series.start.Screening::describe).toList()))
                .variant("step1", r -> run(() -> new pl.training.workshop.exercises.e08_series.step1.SeriesPlanner()
                        .plan(r).stream().map(pl.training.workshop.exercises.e08_series.step1.Screening::describe).toList()))
                .variant("step2", r -> run(() -> new pl.training.workshop.exercises.e08_series.step2.SeriesPlanner()
                        .plan(r).stream().map(pl.training.workshop.exercises.e08_series.step2.Screening::describe).toList()))
                .expect("IMAX, trzy tygodnie, wszystko domyślne",
                        new SeriesRequest("Diuna", "IMAX", null, FRIDAY_EVENING, 3, null, null, null, false, null),
                        """
                        Diuna IMAX sala A 2026-11-06T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+
                        Diuna IMAX sala A 2026-11-13T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+
                        Diuna IMAX sala A 2026-11-20T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+""")
                .expect("3D z dubbingiem, bez limitu wieku, sala B",
                        new SeriesRequest("Kraina Lodu", "3D", "B", SATURDAY_MORNING, 2, null, true, 0, false, null),
                        """
                        Kraina Lodu 3D sala B 2026-11-07T10:30 cena 32.00 doplaty {OKULARY=3.00, VIP=10.00} dubbing 0+
                        Kraina Lodu 3D sala B 2026-11-14T10:30 cena 32.00 doplaty {OKULARY=3.00, VIP=10.00} dubbing 0+""")
                .expect("2D z własną ceną",
                        new SeriesRequest("Amator", "2D", "C", SUNDAY_EVENING, 1, Money.of("20.00"), false, null, false, null),
                        "Amator 2D sala C 2026-11-08T20:00 cena 20.00 doplaty {VIP=10.00} napisy 12+")
                .expect("ostatni tydzień w innej sali",
                        new SeriesRequest("Diuna", "IMAX", "A", FRIDAY_EVENING, 4, null, null, null, false, "D"),
                        """
                        Diuna IMAX sala A 2026-11-06T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+
                        Diuna IMAX sala A 2026-11-13T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+
                        Diuna IMAX sala A 2026-11-20T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+
                        Diuna IMAX sala D 2026-11-27T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+""")
                .expect("jeden tydzień w innej sali: pierwszy seans jest ostatnim",
                        new SeriesRequest("Amator", "2D", null, SUNDAY_EVENING, 1, null, null, null, false, "D"),
                        "Amator 2D sala D 2026-11-08T20:00 cena 25.00 doplaty {VIP=10.00} napisy 12+")
                .expect("premiera przy jednym tygodniu",
                        new SeriesRequest("Diuna", "IMAX", null, FRIDAY_EVENING, 1, null, null, null, true, null),
                        "Diuna IMAX sala A 2026-11-06T18:00 cena 40.00 doplaty {PREMIERA=5.00, VIP=10.00} napisy 12+")
                .expect("własny limit wieku",
                        new SeriesRequest("Diuna", "IMAX", null, FRIDAY_EVENING, 1, null, null, 16, false, null),
                        "Diuna IMAX sala A 2026-11-06T18:00 cena 40.00 doplaty {VIP=10.00} napisy 16+")
                .expect("cena zero jest dozwolona (pokaz darmowy)",
                        new SeriesRequest("Amator", "2D", null, SUNDAY_EVENING, 1, Money.ZERO, null, null, false, null),
                        "Amator 2D sala A 2026-11-08T20:00 cena 0.00 doplaty {VIP=10.00} napisy 12+")
                .expect("zero tygodni",
                        new SeriesRequest("Diuna", "IMAX", null, FRIDAY_EVENING, 0, null, null, null, false, null),
                        "ERROR: weeks must be positive")
                .expect("pusty tytuł",
                        new SeriesRequest("  ", "IMAX", null, FRIDAY_EVENING, 2, null, null, null, false, null),
                        "ERROR: title must not be blank")
                .expect("nieznany format",
                        new SeriesRequest("Diuna", "4DX", null, FRIDAY_EVENING, 2, null, null, null, false, null),
                        "ERROR: unknown format: 4DX")
                .expect("nieznany format mimo własnej ceny",
                        new SeriesRequest("Diuna", "4DX", null, FRIDAY_EVENING, 2, Money.of("50.00"), null, null, false, null),
                        "ERROR: unknown format: 4DX")
                .expect("ujemna cena bazowa",
                        new SeriesRequest("Diuna", "IMAX", null, FRIDAY_EVENING, 2, Money.of("-1.00"), null, null, false, null),
                        "ERROR: base price must not be negative")
                .expect("zero tygodni i pusty tytuł: najpierw tygodnie",
                        new SeriesRequest("", "IMAX", null, FRIDAY_EVENING, 0, null, null, null, false, null),
                        "ERROR: weeks must be positive")
                .expect("pusty tytuł i zły format: najpierw tytuł",
                        new SeriesRequest("", "4DX", null, FRIDAY_EVENING, 1, null, null, null, false, null),
                        "ERROR: title must not be blank")
                .tests();
    }

    static String run(Supplier<List<String>> series) {
        try {
            return String.join("\n", series.get());
        } catch (IllegalArgumentException exception) {
            return "ERROR: " + exception.getMessage();
        }
    }
}
