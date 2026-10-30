package pl.training.workshop.exercises.e10_showtime.start

import pl.training.workshop.exercises.e10_showtime.ShowRequest

/**
 * Start: automat startujący seanse według harmonogramu - kopia orkiestracji z panelu kasjera.
 * Kopia powstała, zanim doszedł raport frekwencji, i nikt jej nie uzupełnił: seanse startowane
 * automatycznie nie trafiają do raportu dla dystrybutora.
 */
class AutoScheduler(
    private val scanner: TicketScanner,
    private val hall: HallController,
    private val report: AttendanceReport
) {
    fun startShow(request: ShowRequest) {
        scanner.close(request.screeningId)
        hall.startShow(request.title)
    }

    fun endShow(screeningId: String) {
        hall.endShow()
        report.close(screeningId)
    }
}
