package pl.training.workshop.m5.s04_extractsuperclass.step2;

import java.time.LocalDateTime;

/** Krok 2: bez zmian. */
public final class Screening extends HallBooking {
    private final String title;

    public Screening(String title, String hall, LocalDateTime start, int minutes) {
        super(hall, start, minutes);
        this.title = title;
    }

    public String title() {
        return title;
    }
}
