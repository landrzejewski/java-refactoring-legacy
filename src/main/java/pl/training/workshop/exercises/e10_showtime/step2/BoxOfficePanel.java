package pl.training.workshop.exercises.e10_showtime.step2;

import pl.training.workshop.exercises.e10_showtime.ShowRequest;

/** Krok 2: panel kasjera zna tylko fasadę - nie wie, z ilu kroków składa się start seansu. */
public final class BoxOfficePanel {
    private final Showtime showtime;

    public BoxOfficePanel(Showtime showtime) {
        this.showtime = showtime;
    }

    public void startShow(ShowRequest request) {
        showtime.start(request);
    }

    public void endShow(String screeningId) {
        showtime.end(screeningId);
    }
}
