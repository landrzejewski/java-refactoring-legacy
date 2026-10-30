package pl.training.workshop.exercises.e10_showtime.step2

import pl.training.workshop.exercises.e10_showtime.ShowRequest

/**
 * Krok 2: automat używa tej samej fasady co panel kasjera. Świadoma zmiana zachowania: seanse
 * startowane automatycznie trafiają teraz do raportu frekwencji (dokumentuje ją E10BehaviourChangeTest).
 */
class AutoScheduler(private val showtime: Showtime) {
    fun startShow(request: ShowRequest) {
        showtime.start(request)
    }

    fun endShow(screeningId: String) {
        showtime.end(screeningId)
    }
}
