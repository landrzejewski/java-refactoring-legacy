package pl.training.workshop.exercises.e10_showtime.step1

/** Krok 1: oświetlenie sali. Jedyne urządzenie bez referencji do innych - reaguje tylko na polecenia. */
class Lights(private val log: DeviceLog) {
    var level = 100
        private set

    fun set(level: Int) {
        if (level < 0 || level > 100) {
            throw IllegalArgumentException("lights level out of range: $level")
        }
        this.level = level
        log.add("lights: $level%")
    }
}
