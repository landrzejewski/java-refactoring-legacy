package pl.training.workshop.m3.s10_isp.start;

import java.time.LocalTime;

/** Klient: tablica seansów. Używa scheduleScreening, cancelScreening i screenings. */
public final class ScheduleBoard {
    private final CinemaAdminService backOffice;

    public ScheduleBoard(CinemaAdminService backOffice) {
        this.backOffice = backOffice;
    }

    public void plan(String title, LocalTime start) {
        backOffice.scheduleScreening(title, start);
    }

    public void cancel(String title) {
        backOffice.cancelScreening(title);
    }

    public String board() {
        return String.join(", ", backOffice.screenings());
    }
}
