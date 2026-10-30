package pl.training.workshop.exercises.e10_showtime.step2

/**
 * Krok 2: projektor zna tylko własny stan. `stop()` to zwykłe zatrzymanie z kontrolą stanu,
 * `cut()` to awaryjne odcięcie lampy - oba są operacjami urządzenia, żadne nie steruje salą.
 */
internal class Projector(private val log: DeviceLog) {
    private var playing = false

    val isPlaying: Boolean
        get() = playing

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
    }

    /** Awaryjne odcięcie lampy - działa w każdym stanie. */
    fun cut() {
        playing = false
        log.add("projector: cut")
    }
}
