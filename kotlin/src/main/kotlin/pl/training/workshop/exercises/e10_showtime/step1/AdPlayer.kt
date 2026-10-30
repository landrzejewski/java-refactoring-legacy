package pl.training.workshop.exercises.e10_showtime.step1

/** Krok 1: odtwarzacz reklam liczy reklamy i zgłasza koniec playlisty - nie wie, co się wtedy dzieje w sali. */
class AdPlayer(private val log: DeviceLog, private val hall: HallMediator) {
    private var remaining = 0

    fun start(ads: Int) {
        remaining = ads
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
        hall.adsFinished()
    }

    fun stop() {
        if (remaining > 0) {
            remaining = 0
            log.add("ads: stopped")
        }
    }
}
