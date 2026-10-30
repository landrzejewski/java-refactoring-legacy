package pl.training.workshop.exercises.e02_hallmap

import java.lang.reflect.Modifier
import java.util.Collections
import java.util.IdentityHashMap

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

import pl.training.workshop.exercises.e02_hallmap.E02EquivalenceTest.Companion.hall
import pl.training.workshop.exercises.e02_hallmap.E02EquivalenceTest.Companion.row
import pl.training.workshop.exercises.e02_hallmap.E02EquivalenceTest.Companion.section
import pl.training.workshop.exercises.e02_hallmap.step3.Hall
import pl.training.workshop.exercises.e02_hallmap.step3.HallElement
import pl.training.workshop.exercises.e02_hallmap.step3.HallGroup
import pl.training.workshop.exercises.e02_hallmap.step3.Row
import pl.training.workshop.exercises.e02_hallmap.step3.Seat
import pl.training.workshop.exercises.e02_hallmap.step3.SeatCategories
import pl.training.workshop.exercises.e02_hallmap.step3.SeatCategory
import pl.training.workshop.exercises.e02_hallmap.step3.Section

/** Kontrakt rozwiązania: jedno drzewo, jedno przejście, jedna instancja kategorii na kod. */
class E02SolutionTest {
    @Test
    fun fourHundredSeatsShareAtMostFourCategoryInstances() {
        val rows = (1..20).map { i -> row("R$i", if (i <= 16) "NNNNNNNNNNNNNNNNNNNN" else "VVVVVVVVVVVVVVVVVVVV") }
        val imax = Hall.of(hall("IMAX", SectionSpec("Parter", rows)))

        val instances: MutableSet<SeatCategory> = Collections.newSetFromMap(IdentityHashMap())
        val seats = mutableListOf<Seat>()
        for (seat in imax) {
            instances += seat.category
            seats += seat
        }

        assertEquals(400, seats.size)
        assertEquals(2, instances.size, "jedna instancja na kod kategorii")
        assertSame(seats[0].category, seats[319].category, "pierwszy i ostatni fotel normalny")
        assertSame(seats[320].category, seats[399].category, "pierwszy i ostatni fotel VIP")
        assertTrue(SeatCategories.known().size <= 4, "fabryka zna najwyzej cztery kategorie")
    }

    @Test
    fun seatKeepsOnlyItsOwnStateAndCategoryKnowsNoPlace() {
        assertEquals(setOf("label", "category"), fieldNames(Seat::class.java))
        assertTrue(
            fieldNames(SeatCategory::class.java).none { it.contains("label") || it.contains("row") },
            "kategoria nie wie, gdzie stoi fotel"
        )
        assertTrue(
            SeatCategory::class.java.declaredFields.all { Modifier.isFinal(it.modifiers) },
            "wspoldzielony stan musi byc niemutowalny"
        )
    }

    @Test
    fun iterationSkipsGapsAndKeepsSectionAndRowOrder() {
        val hall = Hall.of(hall("Mala", section("Parter", row("A", "N_N"), row("B", "_V")), section("Balkon", row("C", "D_"))))
        val labels = mutableListOf<String>()
        hall.forEach { seat -> labels += seat.label }
        assertEquals(listOf("A1", "A3", "B2", "C1"), labels)
    }

    @Test
    fun everyLevelOfTheHallAnswersTheSameQuestions() {
        for (level in listOf(Hall::class.java, Section::class.java, Row::class.java)) {
            assertTrue(HallGroup::class.java.isAssignableFrom(level), "${level.simpleName} jest wezlem drzewa")
        }
        assertTrue(HallElement::class.java.isAssignableFrom(Seat::class.java), "miejsce jest lisciem o tym samym kontrakcie")

        val hall = Hall.of(hall("Mala", section("Parter", row("A", "NV"), row("B", "DD"))))
        var counted = 0
        for (ignored in hall) {
            counted++
        }
        assertEquals(counted, hall.capacity(), "pojemnosc drzewa zgadza sie z liczba odwiedzonych miejsc")
        assertEquals("22.00", hall.surcharges().toString())
    }

    @Test
    fun factoryRejectsUnknownCodeWithTheSameMessageAsStart() {
        val error = assertThrows<IllegalArgumentException> { SeatCategories.of('X') }
        assertEquals("unknown seat category: X", error.message)
    }

    private companion object {
        fun fieldNames(type: Class<*>): Set<String> = type.declaredFields.map { it.name }.toSet()
    }
}
