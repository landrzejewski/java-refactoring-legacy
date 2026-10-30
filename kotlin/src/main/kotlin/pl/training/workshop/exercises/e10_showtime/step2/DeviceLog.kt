package pl.training.workshop.exercises.e10_showtime.step2

/** Krok 2: wspólny dziennik zdarzeń urządzeń i podsystemów - kolejność wpisów jest obserwowalnym zachowaniem sali. */
internal class DeviceLog {
    private val lines = mutableListOf<String>()

    fun add(line: String) {
        lines += line
    }

    fun dump(): String = lines.joinToString("\n")
}
