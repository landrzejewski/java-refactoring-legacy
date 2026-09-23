package pl.training.workshop.m7.s06_parameterobject.step3;

import java.time.LocalDate;
import java.util.Set;

/**
 * Krok 3: walidacja przeniesiona do konstruktora rekordu - niepoprawny termin w ogóle nie powstaje.
 * To ZMIANA KONTRAKTU: wyjątek pojawia się wcześniej (przy tworzeniu obiektu) i także
 * dla describe(), które wcześniej niczego nie sprawdzało. Dlatego osobny krok i osobny commit.
 */
public record ScreeningSlot(String screeningId, LocalDate date, int hall, String format) {
    private static final Set<String> FORMATS = Set.of("2D", "3D", "IMAX");

    public ScreeningSlot {
        if (hall < 1 || hall > 8) {
            throw new IllegalArgumentException("nie ma sali " + hall + " (" + screeningId + ")");
        }
        if (!FORMATS.contains(format)) {
            throw new IllegalArgumentException("nieznany format " + format + " (" + screeningId + ")");
        }
    }

    public String label() {
        return screeningId + " " + date + " sala " + hall + " (" + format + ")";
    }
}
