package pl.training.workshop.exercises.e10_showtime.start

/** Start: wspólny dziennik zdarzeń urządzeń i podsystemów - kolejność wpisów jest obserwowalnym zachowaniem sali. */
class DeviceLog {
    private val lines = mutableListOf<String>()

    fun add(line: String) {
        lines += line
    }

    fun dump(): String = lines.joinToString("\n")
}
