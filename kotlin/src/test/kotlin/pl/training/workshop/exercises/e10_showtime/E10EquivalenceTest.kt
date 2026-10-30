package pl.training.workshop.exercises.e10_showtime

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

import pl.training.workshop.support.Scene

/**
 * Dziennik urządzeń i podsystemów dla scenariuszy wspólnych wszystkim krokom: kolejność reakcji sali,
 * błędy urządzeń, procedura awaryjna. Start z automatu (`auto start`) jest celowo poza tym testem,
 * bo w kroku 2 świadomie zmienia zachowanie - opisuje go E10BehaviourChangeTest.
 */
class E10EquivalenceTest {
    @TestFactory
    fun everyStepLogsTheSameEvents(): List<DynamicTest> = Scene.variants<List<String>, String>()
        .variant("start") { s -> pl.training.workshop.exercises.e10_showtime.start.ShowtimeRunner().run(s) }
        .variant("step1") { s -> pl.training.workshop.exercises.e10_showtime.step1.ShowtimeRunner().run(s) }
        .variant("step2") { s -> pl.training.workshop.exercises.e10_showtime.step2.ShowtimeRunner().run(s) }
        .expect(
            "start z kasy: bramka, drzwi, swiatla, reklamy, raport, film",
            listOf("scan S1", "scan S1", "scan S1", "desk start S1 Diuna", "tick", "tick"),
            """
            scanner: S1 closed, 3 scanned
            doors: closed
            lights: 30%
            ads: start 2
            report: S1 attendance 3
            ads: 1 left
            ads: finished
            lights: 0%
            sound: 70
            projector: play Diuna
            """.trimIndent()
        )
        .expect(
            "koniec seansu z kasy",
            listOf("scan S1", "desk start S1 Diuna", "tick", "tick", "desk end S1"),
            """
            scanner: S1 closed, 1 scanned
            doors: closed
            lights: 30%
            ads: start 2
            report: S1 attendance 1
            ads: 1 left
            ads: finished
            lights: 0%
            sound: 70
            projector: play Diuna
            projector: stop
            sound: 0
            lights: 100%
            doors: open
            report: S1 closed
            """.trimIndent()
        )
        .expect(
            "koniec seansu z automatu jest taki sam jak z kasy",
            listOf("desk start S1 Amator", "tick", "tick", "auto end S1"),
            """
            scanner: S1 closed, 0 scanned
            doors: closed
            lights: 30%
            ads: start 2
            report: S1 attendance 0
            ads: 1 left
            ads: finished
            lights: 0%
            sound: 70
            projector: play Amator
            projector: stop
            sound: 0
            lights: 100%
            doors: open
            report: S1 closed
            """.trimIndent()
        )
        .expect(
            "przeszkoda w drzwiach: swiatla w gore, zamkniecie wznowione po zwolnieniu",
            listOf("obstacle", "desk start S1 Diuna", "cleared", "tick", "tick"),
            """
            doors: obstacle
            scanner: S1 closed, 0 scanned
            doors: blocked
            lights: 100%
            report: S1 attendance 0
            doors: cleared
            doors: closed
            lights: 30%
            ads: start 2
            ads: 1 left
            ads: finished
            lights: 0%
            sound: 70
            projector: play Diuna
            """.trimIndent()
        )
        .expect(
            "przeszkoda po zamknieciu drzwi nie wznawia niczego",
            listOf("desk start S1 Diuna", "obstacle", "cleared", "tick"),
            """
            scanner: S1 closed, 0 scanned
            doors: closed
            lights: 30%
            ads: start 2
            report: S1 attendance 0
            doors: obstacle
            doors: cleared
            ads: 1 left
            """.trimIndent()
        )
        .expect(
            "awaryjne w trakcie reklam: swiatla, drzwi, dzwiek, reklamy, projektor",
            listOf("desk start S1 Diuna", "tick", "emergency", "tick"),
            """
            scanner: S1 closed, 0 scanned
            doors: closed
            lights: 30%
            ads: start 2
            report: S1 attendance 0
            ads: 1 left
            lights: 100%
            doors: open
            sound: 0
            ads: stopped
            projector: cut
            ERROR: no ads playing
            """.trimIndent()
        )
        .expect(
            "awaryjne w trakcie filmu: po odcieciu lampy nie ma czego konczyc",
            listOf("desk start S1 Diuna", "tick", "tick", "emergency", "desk end S1"),
            """
            scanner: S1 closed, 0 scanned
            doors: closed
            lights: 30%
            ads: start 2
            report: S1 attendance 0
            ads: 1 left
            ads: finished
            lights: 0%
            sound: 70
            projector: play Diuna
            lights: 100%
            doors: open
            sound: 0
            projector: cut
            ERROR: projector is not playing
            """.trimIndent()
        )
        .expect(
            "drugi seans przy zamknietych drzwiach: bramka zamknieta, drzwi odmawiaja, raportu brak",
            listOf("scan S1", "desk start S1 Diuna", "desk start S2 Amator"),
            """
            scanner: S1 closed, 1 scanned
            doors: closed
            lights: 30%
            ads: start 2
            report: S1 attendance 1
            scanner: S2 closed, 0 scanned
            ERROR: doors already closed
            """.trimIndent()
        )
        .expect(
            "drugi seans po zakonczeniu pierwszego",
            listOf("desk start S1 Diuna", "tick", "tick", "desk end S1", "desk start S2 Amator"),
            """
            scanner: S1 closed, 0 scanned
            doors: closed
            lights: 30%
            ads: start 2
            report: S1 attendance 0
            ads: 1 left
            ads: finished
            lights: 0%
            sound: 70
            projector: play Diuna
            projector: stop
            sound: 0
            lights: 100%
            doors: open
            report: S1 closed
            scanner: S2 closed, 0 scanned
            doors: closed
            lights: 30%
            ads: start 2
            report: S2 attendance 0
            """.trimIndent()
        )
        .expect("koniec bez seansu", listOf("desk end S1"), "ERROR: projector is not playing")
        .expect(
            "skan po zamknieciu bramki",
            listOf("desk start S1 Diuna", "scan S1"),
            """
            scanner: S1 closed, 0 scanned
            doors: closed
            lights: 30%
            ads: start 2
            report: S1 attendance 0
            ERROR: scanner S1 already closed
            """.trimIndent()
        )
        .expect(
            "ponowny start tego samego seansu",
            listOf("desk start S1 Diuna", "tick", "tick", "desk end S1", "desk start S1 Diuna"),
            """
            scanner: S1 closed, 0 scanned
            doors: closed
            lights: 30%
            ads: start 2
            report: S1 attendance 0
            ads: 1 left
            ads: finished
            lights: 0%
            sound: 70
            projector: play Diuna
            projector: stop
            sound: 0
            lights: 100%
            doors: open
            report: S1 closed
            ERROR: scanner S1 already closed
            """.trimIndent()
        )
        .expect(
            "limit glosnosci jest logika naglosnienia",
            listOf("volume 95", "volume 60"),
            """
            sound: 85 (capped)
            sound: 60
            """.trimIndent()
        )
        .expect("zwolnienie przejscia bez przeszkody", listOf("cleared"), "ERROR: doors are not blocked")
        .expect("reklama bez playlisty", listOf("tick"), "ERROR: no ads playing")
        .expect("nieznane polecenie", listOf("dance"), "ERROR: unknown command: dance")
        .tests()
}
