package pl.training.workshop.m5.s04_extractsuperclass.step1;

import java.time.LocalDateTime;

/**
 * Krok 1: Extract Superclass z klasy Screening. Nazwa opisuje pojęcie domenowe (rezerwacja sali),
 * a nie motywację ("BaseScreening", "AbstractCommon"). Pola prywatne, ustawiane przez super(...).
 */
public abstract class HallBooking {
    private final String hall;
    private final LocalDateTime start;
    private final int minutes;

    protected HallBooking(String hall, LocalDateTime start, int minutes) {
        this.hall = hall;
        this.start = start;
        this.minutes = minutes;
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
