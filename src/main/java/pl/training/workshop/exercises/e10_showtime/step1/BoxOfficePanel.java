package pl.training.workshop.exercises.e10_showtime.step1;

import pl.training.workshop.exercises.e10_showtime.ShowRequest;

/** Krok 1: panel kasjera sam zna kroki startu seansu i ich kolejność: bramka, sala, raport. */
public final class BoxOfficePanel {
    private final TicketScanner scanner;
    private final HallController hall;
    private final AttendanceReport report;

    public BoxOfficePanel(TicketScanner scanner, HallController hall, AttendanceReport report) {
        this.scanner = scanner;
        this.hall = hall;
        this.report = report;
    }

    public void startShow(ShowRequest request) {
        int attendance = scanner.close(request.screeningId());
        hall.startShow(request.title());
        report.record(request.screeningId(), attendance);
    }

    public void endShow(String screeningId) {
        hall.endShow();
        report.close(screeningId);
    }
}
