package pl.training.workshop.exercises.e08_series

import java.time.LocalDateTime

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

import pl.training.workshop.shared.Money
import pl.training.workshop.support.Scene

/**
 * Seria tygodniowa z szablonu: wartości domyślne, własna cena, dubbing, inna sala na koniec,
 * premiera przy jednym tygodniu oraz kolejność błędów walidacji.
 * Scenariusz "premiera przy wielu tygodniach" celowo nie jest tu obecny: start ma w nim błąd
 * płytkiej kopii, który rozwiązanie świadomie naprawia - dokumentuje to [E08SolutionTest].
 */
class E08EquivalenceTest {
    @TestFactory
    fun everyStepPlansTheSameSeries(): List<DynamicTest> = Scene.variants<SeriesRequest, String>()
        .variant("start") { r -> report { pl.training.workshop.exercises.e08_series.start.SeriesPlanner().plan(r).map { it.describe() } } }
        .variant("step1") { r -> report { pl.training.workshop.exercises.e08_series.step1.SeriesPlanner().plan(r).map { it.describe() } } }
        .variant("step2") { r -> report { pl.training.workshop.exercises.e08_series.step2.SeriesPlanner().plan(r).map { it.describe() } } }
        .expect(
            "IMAX, trzy tygodnie, wszystko domyślne",
            SeriesRequest("Diuna", "IMAX", null, FRIDAY_EVENING, 3, null, null, null, false, null),
            """
            Diuna IMAX sala A 2026-11-06T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+
            Diuna IMAX sala A 2026-11-13T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+
            Diuna IMAX sala A 2026-11-20T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+
            """.trimIndent()
        )
        .expect(
            "3D z dubbingiem, bez limitu wieku, sala B",
            SeriesRequest("Kraina Lodu", "3D", "B", SATURDAY_MORNING, 2, null, true, 0, false, null),
            """
            Kraina Lodu 3D sala B 2026-11-07T10:30 cena 32.00 doplaty {OKULARY=3.00, VIP=10.00} dubbing 0+
            Kraina Lodu 3D sala B 2026-11-14T10:30 cena 32.00 doplaty {OKULARY=3.00, VIP=10.00} dubbing 0+
            """.trimIndent()
        )
        .expect(
            "2D z własną ceną",
            SeriesRequest("Amator", "2D", "C", SUNDAY_EVENING, 1, Money.of("20.00"), false, null, false, null),
            "Amator 2D sala C 2026-11-08T20:00 cena 20.00 doplaty {VIP=10.00} napisy 12+"
        )
        .expect(
            "ostatni tydzień w innej sali",
            SeriesRequest("Diuna", "IMAX", "A", FRIDAY_EVENING, 4, null, null, null, false, "D"),
            """
            Diuna IMAX sala A 2026-11-06T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+
            Diuna IMAX sala A 2026-11-13T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+
            Diuna IMAX sala A 2026-11-20T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+
            Diuna IMAX sala D 2026-11-27T18:00 cena 40.00 doplaty {VIP=10.00} napisy 12+
            """.trimIndent()
        )
        .expect(
            "jeden tydzień w innej sali: pierwszy seans jest ostatnim",
            SeriesRequest("Amator", "2D", null, SUNDAY_EVENING, 1, null, null, null, false, "D"),
            "Amator 2D sala D 2026-11-08T20:00 cena 25.00 doplaty {VIP=10.00} napisy 12+"
        )
        .expect(
            "premiera przy jednym tygodniu",
            SeriesRequest("Diuna", "IMAX", null, FRIDAY_EVENING, 1, null, null, null, true, null),
            "Diuna IMAX sala A 2026-11-06T18:00 cena 40.00 doplaty {PREMIERA=5.00, VIP=10.00} napisy 12+"
        )
        .expect(
            "własny limit wieku",
            SeriesRequest("Diuna", "IMAX", null, FRIDAY_EVENING, 1, null, null, 16, false, null),
            "Diuna IMAX sala A 2026-11-06T18:00 cena 40.00 doplaty {VIP=10.00} napisy 16+"
        )
        .expect(
            "cena zero jest dozwolona (pokaz darmowy)",
            SeriesRequest("Amator", "2D", null, SUNDAY_EVENING, 1, Money.ZERO, null, null, false, null),
            "Amator 2D sala A 2026-11-08T20:00 cena 0.00 doplaty {VIP=10.00} napisy 12+"
        )
        .expect(
            "zero tygodni",
            SeriesRequest("Diuna", "IMAX", null, FRIDAY_EVENING, 0, null, null, null, false, null),
            "ERROR: weeks must be positive"
        )
        .expect(
            "pusty tytuł",
            SeriesRequest("  ", "IMAX", null, FRIDAY_EVENING, 2, null, null, null, false, null),
            "ERROR: title must not be blank"
        )
        .expect(
            "nieznany format",
            SeriesRequest("Diuna", "4DX", null, FRIDAY_EVENING, 2, null, null, null, false, null),
            "ERROR: unknown format: 4DX"
        )
        .expect(
            "nieznany format mimo własnej ceny",
            SeriesRequest("Diuna", "4DX", null, FRIDAY_EVENING, 2, Money.of("50.00"), null, null, false, null),
            "ERROR: unknown format: 4DX"
        )
        .expect(
            "ujemna cena bazowa",
            SeriesRequest("Diuna", "IMAX", null, FRIDAY_EVENING, 2, Money.of("-1.00"), null, null, false, null),
            "ERROR: base price must not be negative"
        )
        .expect(
            "zero tygodni i pusty tytuł: najpierw tygodnie",
            SeriesRequest("", "IMAX", null, FRIDAY_EVENING, 0, null, null, null, false, null),
            "ERROR: weeks must be positive"
        )
        .expect(
            "pusty tytuł i zły format: najpierw tytuł",
            SeriesRequest("", "4DX", null, FRIDAY_EVENING, 1, null, null, null, false, null),
            "ERROR: title must not be blank"
        )
        .tests()

    companion object {
        private val FRIDAY_EVENING = LocalDateTime.of(2026, 11, 6, 18, 0)
        private val SATURDAY_MORNING = LocalDateTime.of(2026, 11, 7, 10, 30)
        private val SUNDAY_EVENING = LocalDateTime.of(2026, 11, 8, 20, 0)

        fun report(series: () -> List<String>): String = try {
            series().joinToString("\n")
        } catch (exception: IllegalArgumentException) {
            "ERROR: ${exception.message}"
        }
    }
}
