package pl.training.workshop.exercises.e10_showtime.start

/**
 * Start: projektor. `stop()` kończy seans i "przy okazji" przywraca dźwięk, światła i drzwi,
 * więc zna trzy inne urządzenia. `cut()` powstał dla procedury awaryjnej, bo tam kaskada
 * po `stop()` dublowała wpisy w dzienniku - dwa sposoby wyłączenia jednego urządzenia.
 */
class Projector(private val log: DeviceLog) {
    private lateinit var sound: Sound
    private lateinit var lights: Lights
    private lateinit var doors: Doors
    private var playing = false

    val isPlaying: Boolean
        get() = playing

    fun wire(sound: Sound, lights: Lights, doors: Doors) {
        this.sound = sound
        this.lights = lights
        this.doors = doors
    }

    fun play(title: String?) {
        if (playing) {
            throw IllegalStateException("projector already playing")
        }
        playing = true
        log.add("projector: play $title")
    }

    fun stop() {
        if (!playing) {
            throw IllegalStateException("projector is not playing")
        }
        playing = false
        log.add("projector: stop")
        sound.set(0)
        lights.set(100)
        doors.open()
    }

    /** Awaryjne odcięcie lampy - bez kaskady. */
    fun cut() {
        playing = false
        log.add("projector: cut")
    }
}
