package pl.training.workshop.exercises.e10_showtime.start

/**
 * Start: odtwarzacz reklam. Gdy skończy playlistę, sam gasi światła, ustawia dźwięk i uruchamia projektor,
 * więc musi znać trzy inne urządzenia i tytuł filmu.
 */
class AdPlayer(private val log: DeviceLog) {
    private lateinit var lights: Lights
    private lateinit var sound: Sound
    private lateinit var projector: Projector
    private var remaining = 0
    private var title: String? = null

    fun wire(lights: Lights, sound: Sound, projector: Projector) {
        this.lights = lights
        this.sound = sound
        this.projector = projector
    }

    fun start(ads: Int, title: String) {
        remaining = ads
        this.title = title
        log.add("ads: start $ads")
    }

    /** Sprzęt: kolejna reklama się skończyła. */
    fun tick() {
        if (remaining <= 0) {
            throw IllegalStateException("no ads playing")
        }
        remaining--
        if (remaining > 0) {
            log.add("ads: $remaining left")
            return
        }
        log.add("ads: finished")
        lights.set(0)
        sound.set(70)
        projector.play(title)
        title = null
    }

    fun stop() {
        if (remaining > 0) {
            remaining = 0
            title = null
            log.add("ads: stopped")
        }
    }
}
