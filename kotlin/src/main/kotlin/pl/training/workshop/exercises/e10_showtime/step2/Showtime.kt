package pl.training.workshop.exercises.e10_showtime.step2

import pl.training.workshop.exercises.e10_showtime.ShowRequest

/**
 * Krok 2: jedna operacja "uruchom seans" zamiast znajomości kroków i ich kolejności u każdego klienta.
 * Fasada nie koordynuje reakcji urządzeń (to robi sterownik sali) - tylko upraszcza wejście do
 * podsystemów i pilnuje, że żadnego kroku nie da się pominąć. Składa ją korzeń kompozycji.
 */
class Showtime internal constructor(
    private val scanner: TicketScanner,
    private val hall: HallController,
    private val report: AttendanceReport
) {
    fun start(request: ShowRequest) {
        val attendance = scanner.close(request.screeningId)
        hall.startShow(request.title)
        report.record(request.screeningId, attendance)
    }

    fun end(screeningId: String) {
        hall.endShow()
        report.close(screeningId)
    }
}
