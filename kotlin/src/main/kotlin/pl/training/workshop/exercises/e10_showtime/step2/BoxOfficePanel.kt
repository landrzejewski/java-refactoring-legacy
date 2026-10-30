package pl.training.workshop.exercises.e10_showtime.step2

import pl.training.workshop.exercises.e10_showtime.ShowRequest

/** Krok 2: panel kasjera zna tylko fasadę - nie wie, z ilu kroków składa się start seansu. */
class BoxOfficePanel(private val showtime: Showtime) {
    fun startShow(request: ShowRequest) {
        showtime.start(request)
    }

    fun endShow(screeningId: String) {
        showtime.end(screeningId)
    }
}
