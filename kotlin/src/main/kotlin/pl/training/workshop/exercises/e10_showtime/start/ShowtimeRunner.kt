package pl.training.workshop.exercises.e10_showtime.start

import pl.training.workshop.exercises.e10_showtime.ShowRequest

/**
 * Start: korzeń kompozycji i symulator dnia w sali. Skrypt to polecenia klientów
 * (`desk start S1 Diuna`, `auto end S1`, `scan S1`) i zdarzenia sprzętu
 * (`tick`, `obstacle`, `cleared`, `volume 95`, `emergency`).
 * Wynikiem jest dziennik urządzeń; błąd polecenia to wpis `ERROR: ...`.
 */
class ShowtimeRunner {
    private val log = DeviceLog()
    private val scanner = TicketScanner(log)
    private val hall = HallController(log)
    private val report = AttendanceReport(log)
    private val desk = BoxOfficePanel(scanner, hall, report)
    private val auto = AutoScheduler(scanner, hall, report)

    fun run(script: List<String>): String {
        for (line in script) {
            try {
                execute(line.trim().split(WHITESPACE))
            } catch (exception: IllegalStateException) {
                log.add("ERROR: ${exception.message}")
            } catch (exception: IllegalArgumentException) {
                log.add("ERROR: ${exception.message}")
            }
        }
        return log.dump()
    }

    private fun execute(words: List<String>) {
        when (words[0]) {
            "scan" -> scanner.scan(words[1])
            "desk" -> client(words, desk::startShow, desk::endShow)
            "auto" -> client(words, auto::startShow, auto::endShow)
            "tick" -> hall.adTick()
            "obstacle" -> hall.doorObstacle()
            "cleared" -> hall.doorCleared()
            "volume" -> hall.volume(words[1].toInt())
            "emergency" -> hall.emergency()
            else -> throw IllegalArgumentException("unknown command: ${words.joinToString(" ")}")
        }
    }

    private companion object {
        val WHITESPACE = Regex("\\s+")

        fun client(words: List<String>, start: (ShowRequest) -> Unit, end: (String) -> Unit) {
            when (words[1]) {
                "start" -> start(ShowRequest(words[2], words[3]))
                "end" -> end(words[2])
                else -> throw IllegalArgumentException("unknown command: ${words.joinToString(" ")}")
            }
        }
    }
}
