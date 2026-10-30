package pl.training.workshop.exercises.e10_showtime.step1

/** Krok 1: nagłośnienie z limitem głośności (ochrona słuchu) - limit jest logiką urządzenia, nie sali. */
class Sound(private val log: DeviceLog) {
    var level = 0
        private set

    fun set(requested: Int) {
        val applied = minOf(requested, MAX_LEVEL)
        level = applied
        log.add(if (applied < requested) "sound: $applied (capped)" else "sound: $applied")
    }

    companion object {
        const val MAX_LEVEL = 85
    }
}
