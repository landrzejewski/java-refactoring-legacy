package pl.training.workshop.exercises.e10_showtime.step1

/** Krok 1: bramka biletowa - liczy skany i zamyka wejście na seans. */
class TicketScanner(private val log: DeviceLog) {
    private val scanned = HashMap<String, Int>()
    private val closed = HashSet<String>()

    fun scan(screeningId: String) {
        if (screeningId in closed) {
            throw IllegalStateException("scanner $screeningId already closed")
        }
        scanned.merge(screeningId, 1, Int::plus)
    }

    /** Zamyka wejście i zwraca liczbę wpuszczonych widzów. */
    fun close(screeningId: String): Int {
        if (!closed.add(screeningId)) {
            throw IllegalStateException("scanner $screeningId already closed")
        }
        val count = scanned.getOrDefault(screeningId, 0)
        log.add("scanner: $screeningId closed, $count scanned")
        return count
    }
}
