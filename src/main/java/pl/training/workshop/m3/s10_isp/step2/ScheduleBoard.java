package pl.training.workshop.m3.s10_isp.step2;

import java.time.LocalTime;

/** Tablica zależy tylko od roli ScreeningSchedule. */
public final class ScheduleBoard {
    private final ScreeningSchedule backOffice;

    public ScheduleBoard(ScreeningSchedule backOffice) {
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
