package pl.training.workshop.exercises.e10_showtime.step2

/** Krok 2: oświetlenie sali. Jedyne urządzenie bez referencji do innych - reaguje tylko na polecenia. */
internal class Lights(private val log: DeviceLog) {
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
