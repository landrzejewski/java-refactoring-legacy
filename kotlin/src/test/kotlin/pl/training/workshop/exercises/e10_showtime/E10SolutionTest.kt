package pl.training.workshop.exercises.e10_showtime

import java.lang.reflect.Modifier
import kotlin.reflect.KVisibility

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

import pl.training.workshop.exercises.e10_showtime.step2.AutoScheduler
import pl.training.workshop.exercises.e10_showtime.step2.BoxOfficePanel
import pl.training.workshop.exercises.e10_showtime.step2.Showtime

/**
 * Krok 2: urządzenia znają tylko sterownik, kolejność reakcji jest w jednym miejscu,
 * a klienci znają tylko fasadę; podsystemy nie są publiczne.
 */
class E10SolutionTest {
    @Test
    fun noDeviceHoldsAnotherDevice() {
        val allowed = setOf("HallMediator", "DeviceLog", "int", "boolean")
        for (device in DEVICES) {
            for (field in Class.forName(STEP2 + device).declaredFields) {
                if (Modifier.isStatic(field.modifiers)) {
                    continue
                }
                assertTrue(field.type.simpleName in allowed, "$device zna ${field.type.simpleName}")
            }
        }
    }

    @Test
    fun onlyTheHallControllerKnowsTheDevices() {
        val owners = mutableListOf<String>()
        for (name in listOf("HallController", "TicketScanner", "AttendanceReport", "Showtime", "BoxOfficePanel", "AutoScheduler", "ShowtimeRunner")) {
            val holdsDevice = Class.forName(STEP2 + name).declaredFields.any { it.type.simpleName in DEVICES }
            if (holdsDevice) {
                owners += name
            }
        }
        assertEquals(listOf("HallController"), owners, "kolejność reakcji urządzeń w jednym miejscu")
    }

    @Test
    fun clientsKnowOnlyTheFacade() {
        for (client in listOf(BoxOfficePanel::class.java, AutoScheduler::class.java)) {
            val dependencies = client.declaredFields
                .filterNot { Modifier.isStatic(it.modifiers) }
                .map { it.type }
            assertEquals(listOf(Showtime::class.java), dependencies, client.simpleName)
        }
    }

    @Test
    fun subsystemsAreHiddenBehindTheFacade() {
        // Różnica względem Javy: odpowiednikiem klasy pakietowej jest `internal`, a ten w bajtkodzie
        // jest publiczny (tak samo konstruktor `internal`), więc Modifier.isPublic i getConstructors
        // nic tu nie powiedzą. Widoczność w sensie Kotlina podaje kotlin-reflect.
        for (name in SUBSYSTEM) {
            assertFalse(Class.forName(STEP2 + name).kotlin.visibility == KVisibility.PUBLIC, name)
        }
        assertEquals(
            0,
            Showtime::class.constructors.count { it.visibility == KVisibility.PUBLIC },
            "fasadę składa korzeń kompozycji"
        )
    }

    companion object {
        private const val STEP2 = "pl.training.workshop.exercises.e10_showtime.step2."
        private val DEVICES = listOf("Lights", "Sound", "Doors", "AdPlayer", "Projector")
        private val SUBSYSTEM = listOf(
            "TicketScanner", "AttendanceReport", "HallController",
            "HallMediator", "DeviceLog", "Lights", "Sound", "Doors", "AdPlayer", "Projector"
        )
    }
}
