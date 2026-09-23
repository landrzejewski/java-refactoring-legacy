package pl.training.workshop.m5.s04_extractsuperclass.start;

import java.time.LocalDateTime;

/** Start: seans - sala, początek, czas trwania i koniec. PrivateEvent ma to samo, osobno. */
public final class Screening {
    private final String title;
    private final String hall;
    private final LocalDateTime start;
    private final int minutes;

    public Screening(String title, String hall, LocalDateTime start, int minutes) {
        this.title = title;
        this.hall = hall;
        this.start = start;
        this.minutes = minutes;
    }

    public String title() {
        return title;
    }

    public String hall() {
        return hall;
    }

    public LocalDateTime start() {
        return start;
    }

    public LocalDateTime end() {
        return start.plusMinutes(minutes);
    }
}
