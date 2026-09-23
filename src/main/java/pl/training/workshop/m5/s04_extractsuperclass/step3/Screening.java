package pl.training.workshop.m5.s04_extractsuperclass.step3;

import java.time.LocalDateTime;

/** Krok 3: Screening podaje tylko swoją nazwę do opisu konfliktu. */
public final class Screening extends HallBooking {
    private final String title;

    public Screening(String title, String hall, LocalDateTime start, int minutes) {
        super(hall, start, minutes);
        this.title = title;
    }

    public String title() {
        return title;
    }

    @Override
    public String name() {
        return title;
    }
}
