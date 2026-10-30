package pl.training.workshop.exercises.e08_series

import java.time.LocalDateTime

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

import pl.training.workshop.exercises.e08_series.step2.Screening
import pl.training.workshop.exercises.e08_series.step2.SeriesPlanner
import pl.training.workshop.shared.Money

/**
 * Pułapka startu (współdzielona mapa dopłat) i kontrakt rozwiązania:
 * niezależne kopie, wartości domyślne w jednym miejscu, pola wymagane sprawdzane przy budowie.
 */
class E08SolutionTest {
    /** Pomiar w start: kopie współdzielą mapę dopłat. Po "warsztat.sh jump 1" (start ma już kopie) test jest pomijany. */
    @Test
    fun startLeaksThePremiereSurchargeIntoEveryWeekBecauseCopiesShareTheMap() {
        val startCopies = pl.training.workshop.exercises.e08_series.start.Screening::class.java.methods
            .any { it.name == "withSurcharge" }
        assumeTrue(!startCopies, "start ma już niezależne kopie")
        val weeks = pl.training.workshop.exercises.e08_series.start.SeriesPlanner().plan(PREMIERE_SERIES)
        assertEquals(
            listOf(true, true, true),
            weeks.map { it.surcharges.containsKey("PREMIERA") },
            "start: dopłata premierowa trafia do wszystkich tygodni (to błąd, nie kontrakt)"
        )
    }

    @Test
    fun solutionKeepsThePremiereSurchargeInTheFirstWeekOnly() {
        val weeks = SeriesPlanner().plan(PREMIERE_SERIES)
        assertEquals(
            listOf(true, false, false),
            weeks.map { it.surcharges.containsKey("PREMIERA") },
            "rozwiązanie: świadoma naprawa, każdy tydzień ma własną mapę dopłat"
        )
    }

    @Test
    fun copyIsIndependentFromTheTemplate() {
        val template = Screening.builder().title("Diuna").format("IMAX").start(FRIDAY_EVENING).build()
        val premiere = template.withSurcharge("PREMIERA", Money.of("5.00"))
        val nextWeek = template.withStart(FRIDAY_EVENING.plusWeeks(1))

        assertFalse(template.surcharges.containsKey("PREMIERA"), "szablon nie widzi zmiany w kopii")
        assertTrue(premiere.surcharges.containsKey("PREMIERA"))
        assertEquals(template.surcharges, nextWeek.surcharges)
        // Różnica względem Javy: Map z Kotlina nie ma put, więc rzutujemy na MutableMap,
        // żeby sprawdzić, że mapa jest niemodyfikowalna także w czasie działania.
        assertThrows<UnsupportedOperationException>("mapy dopłat nie da się zmienić z zewnątrz") {
            (template.surcharges as MutableMap<String, Money>)["VIP"] = Money.ZERO
        }
    }

    @Test
    fun defaultsLiveInTheBuilderOnly() {
        val screening = Screening.builder().title("Kraina Lodu").format("3D").start(FRIDAY_EVENING).build()
        assertEquals("A", screening.hall)
        assertEquals(Money.of("32.00"), screening.basePrice)
        assertEquals(mapOf("VIP" to Money.of("10.00"), "OKULARY" to Money.of("3.00")), screening.surcharges)
        assertTrue(screening.subtitles)
        assertFalse(screening.dubbing)
        assertEquals(12, screening.ageLimit)
    }

    @Test
    fun builderRejectsMissingRequiredFieldsWithAClearMessage() {
        assertEquals("title must not be blank", assertThrows<IllegalArgumentException> {
            Screening.builder().format("2D").start(FRIDAY_EVENING).build()
        }.message)
        assertEquals("unknown format: null", assertThrows<IllegalArgumentException> {
            Screening.builder().title("Amator").start(FRIDAY_EVENING).build()
        }.message)
        assertEquals("start is required", assertThrows<IllegalArgumentException> {
            Screening.builder().title("Amator").format("2D").build()
        }.message)
    }

    @Test
    fun screeningCannotBeBuiltAroundTheBuilderWithNulls() {
        // Różnica względem Javy: prywatny konstruktor używany przez Builder dostaje od Kotlina
        // publiczny syntetyczny odpowiednik (z DefaultConstructorMarker), niewidoczny z kodu - pomijamy go.
        assertEquals(0, Screening::class.java.constructors.count { !it.isSynthetic }, "jedyna droga to Screening.builder()")
    }

    companion object {
        private val FRIDAY_EVENING = LocalDateTime.of(2026, 11, 6, 18, 0)
        private val PREMIERE_SERIES = SeriesRequest("Diuna", "IMAX", null, FRIDAY_EVENING, 3, null, null, null, true, null)
    }
}
