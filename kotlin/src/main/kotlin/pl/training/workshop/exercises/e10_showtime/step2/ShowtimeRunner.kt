package pl.training.workshop.exercises.e10_showtime.step2

import pl.training.workshop.exercises.e10_showtime.ShowRequest

/**
 * Krok 2: korzeń kompozycji składa podsystemy i fasadę; klienci dostają tylko fasadę.
 * Zdarzenia sprzętu (`tick`, `obstacle`, `cleared`, `volume`, `emergency`)
 * trafiają do sterownika sali bezpośrednio - to symulacja czujników, nie operacja klienta.
 */
class ShowtimeRunner {
    private val log = DeviceLog()
    private val scanner = TicketScanner(log)
    private val hall = HallController(log)
    private val showtime = Showtime(scanner, hall, AttendanceReport(log))
    private val desk = BoxOfficePanel(showtime)
    private val auto = AutoScheduler(showtime)

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
