package pl.training.workshop.exercises.e06_movieinfo;

import java.math.BigDecimal;

/** Stabilny kontrakt sceny: dane filmu w języku kina (minuty, ocena 0-10, kategoria wiekowa kina). */
public record MovieDetails(String title, int year, int runtimeMinutes, BigDecimal rating, String ageCategory) {
    public String describe() {
        return title + " (" + year + "), " + runtimeMinutes + " min, ocena " + rating.toPlainString()
                + "/10, kategoria " + ageCategory;
    }
}
