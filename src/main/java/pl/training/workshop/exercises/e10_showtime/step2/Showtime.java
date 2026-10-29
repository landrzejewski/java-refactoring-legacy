package pl.training.workshop.exercises.e10_showtime.step2;

import pl.training.workshop.exercises.e10_showtime.ShowRequest;

/**
 * Krok 2: jedna operacja "uruchom seans" zamiast znajomości kroków i ich kolejności u każdego klienta.
 * Fasada nie koordynuje reakcji urządzeń (to robi sterownik sali) - tylko upraszcza wejście do
 * podsystemów i pilnuje, że żadnego kroku nie da się pominąć. Składa ją korzeń kompozycji.
 */
public final class Showtime {
    private final TicketScanner scanner;
    private final HallController hall;
    private final AttendanceReport report;

    Showtime(TicketScanner scanner, HallController hall, AttendanceReport report) {
        this.scanner = scanner;
        this.hall = hall;
        this.report = report;
    }

    public void start(ShowRequest request) {
        int attendance = scanner.close(request.screeningId());
        hall.startShow(request.title());
        report.record(request.screeningId(), attendance);
    }

    public void end(String screeningId) {
        hall.endShow();
        report.close(screeningId);
    }
}
