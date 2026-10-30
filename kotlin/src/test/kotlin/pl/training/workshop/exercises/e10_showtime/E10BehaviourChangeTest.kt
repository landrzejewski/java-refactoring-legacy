package pl.training.workshop.exercises.e10_showtime

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test

/**
 * Sens ćwiczenia: kopia orkiestracji w automacie pomija raport frekwencji. Fasada w kroku 2
 * to naprawia - to świadoma zmiana zachowania, nie "czysta" refaktoryzacja, dlatego ma osobny test.
 */
class E10BehaviourChangeTest {
    /** Po "warsztat.sh jump" do kroku 2 automat nie ma już własnej orkiestracji i test jest pomijany. */
    @Test
    fun startAutoSchedulerSkipsTheAttendanceReport() {
        val ownOrchestration = pl.training.workshop.exercises.e10_showtime.start.AutoScheduler::class.java.declaredFields
            .any { it.type.simpleName == "TicketScanner" }
        assumeTrue(ownOrchestration, "start automatu korzysta już z fasady")
        assertEquals(WITHOUT_REPORT, pl.training.workshop.exercises.e10_showtime.start.ShowtimeRunner().run(AUTO_START))
    }

    @Test
    fun step1StillSkipsTheReportBecauseOnlyTheHallChanged() {
        assertEquals(WITHOUT_REPORT, pl.training.workshop.exercises.e10_showtime.step1.ShowtimeRunner().run(AUTO_START))
    }

    @Test
    fun step2FacadeReportsAttendanceForBothClients() {
        assertEquals(WITH_REPORT, pl.training.workshop.exercises.e10_showtime.step2.ShowtimeRunner().run(AUTO_START))
        assertEquals(
            WITH_REPORT,
            pl.training.workshop.exercises.e10_showtime.step2.ShowtimeRunner()
                .run(listOf("scan S1", "scan S1", "desk start S1 Diuna", "tick", "tick"))
        )
    }

    companion object {
        private val AUTO_START = listOf("scan S1", "scan S1", "auto start S1 Diuna", "tick", "tick")

        private val WITHOUT_REPORT = """
            scanner: S1 closed, 2 scanned
            doors: closed
            lights: 30%
            ads: start 2
            ads: 1 left
            ads: finished
            lights: 0%
            sound: 70
            projector: play Diuna
        """.trimIndent()

        private val WITH_REPORT = """
            scanner: S1 closed, 2 scanned
            doors: closed
            lights: 30%
            ads: start 2
            report: S1 attendance 2
            ads: 1 left
            ads: finished
            lights: 0%
            sound: 70
            projector: play Diuna
        """.trimIndent()
    }
}
