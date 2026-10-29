package pl.training.workshop.exercises.e10_showtime.step1;

import pl.training.workshop.exercises.e10_showtime.ShowRequest;

/**
 * Krok 1: automat startujący seanse według harmonogramu - kopia orkiestracji z panelu kasjera.
 * Kopia powstała, zanim doszedł raport frekwencji, i nikt jej nie uzupełnił: seanse startowane
 * automatycznie nie trafiają do raportu dla dystrybutora.
 */
public final class AutoScheduler {
    private final TicketScanner scanner;
    private final HallController hall;
    private final AttendanceReport report;

    public AutoScheduler(TicketScanner scanner, HallController hall, AttendanceReport report) {
        this.scanner = scanner;
        this.hall = hall;
        this.report = report;
    }

    public void startShow(ShowRequest request) {
        scanner.close(request.screeningId());
        hall.startShow(request.title());
    }

    public void endShow(String screeningId) {
        hall.endShow();
        report.close(screeningId);
    }
}
