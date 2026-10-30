package pl.training.workshop.exercises.e02_hallmap

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

import pl.training.workshop.support.Scene

/**
 * Opis sali: pojemność, dopłaty, zliczenie kategorii i lista miejsc w kolejności sektorów
 * i rzędów z pominięciem luk. Błędy danych (nieznany kod, pusty rząd, sektor, sala) też są kontraktem.
 */
class E02EquivalenceTest {
    @TestFactory
    fun everyStepDescribesTheHallTheSame(): List<DynamicTest> = Scene.variants<HallSpec, String>()
        .variant("start") { s -> report { pl.training.workshop.exercises.e02_hallmap.start.HallMaps().describe(s) } }
        .variant("step1") { s -> report { pl.training.workshop.exercises.e02_hallmap.step1.HallMaps().describe(s) } }
        .variant("step2") { s -> report { pl.training.workshop.exercises.e02_hallmap.step2.HallMaps().describe(s) } }
        .variant("step3") { s -> report { pl.training.workshop.exercises.e02_hallmap.step3.HallMaps().describe(s) } }
        .expect(
            "jeden rzad bez luk", hall("Mala", section("Parter", row("A", "NNN"))),
            """
            Sala Mala: pojemnosc 3, doplaty 0.00
            Kategorie: Normalne=3
            Miejsca: A1 o, A2 o, A3 o
            """.trimIndent()
        )
        .expect(
            "luka zuzywa numer", hall("Mala", section("Parter", row("A", "NN_VV"))),
            """
            Sala Mala: pojemnosc 4, doplaty 20.00
            Kategorie: Normalne=2 VIP=2
            Miejsca: A1 o, A2 o, A4 *, A5 *
            """.trimIndent()
        )
        .expect(
            "luka na poczatku rzedu", hall("Mala", section("Parter", row("A", "_NN"))),
            """
            Sala Mala: pojemnosc 2, doplaty 0.00
            Kategorie: Normalne=2
            Miejsca: A2 o, A3 o
            """.trimIndent()
        )
        .expect(
            "dwa sektory, powtorzona etykieta rzedu",
            hall("Amator", section("Parter", row("A", "NN"), row("B", "VV")), section("Balkon", row("A", "DD"))),
            """
            Sala Amator: pojemnosc 6, doplaty 32.00
            Kategorie: Normalne=2 VIP=2 Dwuosobowe=2
            Miejsca: A1 o, A2 o, B1 *, B2 *, A1 =, A2 =
            """.trimIndent()
        )
        .expect(
            "wozek i miejsca dwuosobowe", hall("Mala", section("Parter", row("A", "WNNDD"))),
            """
            Sala Mala: pojemnosc 5, doplaty 12.00
            Kategorie: Wozek=1 Normalne=2 Dwuosobowe=2
            Miejsca: A1 &, A2 o, A3 o, A4 =, A5 =
            """.trimIndent()
        )
        .expect(
            "same VIP", hall("Loza", section("Parter", row("A", "VVVV"))),
            """
            Sala Loza: pojemnosc 4, doplaty 40.00
            Kategorie: VIP=4
            Miejsca: A1 *, A2 *, A3 *, A4 *
            """.trimIndent()
        )
        .expect(
            "sala IMAX w skrocie",
            hall(
                "IMAX",
                section("Parter", row("A", "NN_NN"), row("B", "NN_NN"), row("C", "VV_VV")),
                section("Balkon", row("D", "DD_DD"), row("E", "W_NNN"))
            ),
            """
            Sala IMAX: pojemnosc 20, doplaty 64.00
            Kategorie: Normalne=11 VIP=4 Dwuosobowe=4 Wozek=1
            Miejsca: A1 o, A2 o, A4 o, A5 o, B1 o, B2 o, B4 o, B5 o, C1 *, C2 *, C4 *, C5 *, D1 =, D2 =, D4 =, D5 =, E1 &, E3 o, E4 o, E5 o
            """.trimIndent()
        )
        .expect("mala litera to nieznany kod", hall("Mala", section("Parter", row("A", "NvN"))), "ERROR: unknown seat category: v")
        .expect("nieznany kod", hall("Mala", section("Parter", row("A", "NXN"))), "ERROR: unknown seat category: X")
        .expect(
            "blad w drugim sektorze po poprawnym pierwszym",
            hall("Mala", section("Parter", row("A", "NN")), section("Balkon", row("A", "NQ"))),
            "ERROR: unknown seat category: Q"
        )
        .expect("rzad z samych przejsc", hall("Mala", section("Parter", row("A", "___"))), "ERROR: row A has no seats")
        .expect("pusty uklad rzedu", hall("Mala", section("Parter", row("A", ""))), "ERROR: row A has no seats")
        .expect(
            "sektor bez rzedow",
            hall("Mala", section("Parter", row("A", "NN")), section("Balkon")),
            "ERROR: section Balkon has no rows"
        )
        .expect("sala bez sektorow", hall("Pusta"), "ERROR: hall Pusta has no sections")
        .tests()

    companion object {
        fun hall(name: String, vararg sections: SectionSpec) = HallSpec(name, sections.toList())

        fun section(name: String, vararg rows: RowSpec) = SectionSpec(name, rows.toList())

        fun row(label: String, layout: String) = RowSpec(label, layout)

        private fun report(description: () -> String): String = try {
            description()
        } catch (exception: IllegalArgumentException) {
            "ERROR: ${exception.message}"
        }
    }
}
