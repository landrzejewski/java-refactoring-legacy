package pl.training.workshop.exercises.e01_import;

import java.time.LocalTime;

import pl.training.workshop.shared.Money;

/** Pozycja zaimportowanego repertuaru: seans z wyliczoną ceną bazową biletu normalnego. */
public record ScreeningEntry(LocalTime start, String title, String format, String hall, Money price) {
    @Override
    public String toString() {
        return start + " " + title + " (" + format + ", sala " + hall + ") " + price;
    }
}
