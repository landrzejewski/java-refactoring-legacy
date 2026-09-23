package pl.training.workshop.m5.s04_extractsuperclass.step1;

import java.time.LocalDateTime;

/** Krok 1: Screening dołączony do HallBooking - publiczny konstruktor bez zmian, woła super(...). */
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
