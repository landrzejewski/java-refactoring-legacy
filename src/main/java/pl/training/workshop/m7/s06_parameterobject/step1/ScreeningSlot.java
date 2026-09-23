package pl.training.workshop.m7.s06_parameterobject.step1;

import java.time.LocalDate;

/** Krok 1: Parameter Object nazywa pojęcie "termin seansu w sali". Stan przejściowy: bez walidacji. */
public record ScreeningSlot(String screeningId, LocalDate date, int hall, String format) {
}
