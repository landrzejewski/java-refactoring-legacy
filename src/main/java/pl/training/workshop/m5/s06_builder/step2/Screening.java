package pl.training.workshop.m5.s06_builder.step2;

import java.time.LocalTime;

/** Krok 2: liść jako rekord. */
public record Screening(String title, LocalTime start) {
    public String render() {
        return "  " + start + " " + title + "\n";
    }
}
