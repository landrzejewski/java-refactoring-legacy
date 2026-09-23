package pl.training.workshop.m6.s06_builder.step1;

import java.time.LocalTime;

/** Krok 1: bez zmian - liść drzewa repertuaru. */
public final class Screening {
    private final String title;
    private final LocalTime start;

    public Screening(String title, LocalTime start) {
        this.title = title;
        this.start = start;
    }

    public String render() {
        return "  " + start + " " + title + "\n";
    }
}
