package pl.training.workshop.m7.s06_parameterobject.step2;

import java.time.LocalDate;

/** Krok 2: Move Method - typ przyciąga zachowanie: opis terminu należy do terminu. */
public record ScreeningSlot(String screeningId, LocalDate date, int hall, String format) {
    public String label() {
        return screeningId + " " + date + " sala " + hall + " (" + format + ")";
    }
}
