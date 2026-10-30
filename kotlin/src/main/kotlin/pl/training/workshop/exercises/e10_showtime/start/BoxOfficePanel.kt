package pl.training.workshop.exercises.e10_showtime.start

import pl.training.workshop.exercises.e10_showtime.ShowRequest

/** Start: panel kasjera sam zna kroki startu seansu i ich kolejność: bramka, sala, raport. */
class BoxOfficePanel(
    private val scanner: TicketScanner,
    private val hall: HallController,
    private val report: AttendanceReport
) {
    fun startShow(request: ShowRequest) {
        val attendance = scanner.close(request.screeningId)
        hall.startShow(request.title)
        report.record(request.screeningId, attendance)
    }

    fun endShow(screeningId: String) {
        hall.endShow()
        report.close(screeningId)
    }
}
