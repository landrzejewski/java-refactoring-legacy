package pl.training.workshop.m7.s06_parameterobject.start;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Start: data clump - screeningId, date, hall, format zawsze podróżują razem.
 * Łatwo zamienić kolejność argumentów, a walidacja siedzi tylko w ticketPrice().
 */
public final class ScreeningPlanner {
    public String describe(String screeningId, LocalDate date, int hall, String format) {
        return screeningId + " " + date + " sala " + hall + " (" + format + ")";
    }

    public BigDecimal ticketPrice(String screeningId, LocalDate date, int hall, String format) {
        if (hall < 1 || hall > 8) {
            throw new IllegalArgumentException("nie ma sali " + hall + " (" + screeningId + ")");
        }
        return switch (format) {
            case "2D" -> new BigDecimal("25.00");
            case "3D" -> new BigDecimal("32.00");
            case "IMAX" -> new BigDecimal("40.00");
            default -> throw new IllegalArgumentException(
                    "nieznany format " + format + " (" + screeningId + ")");
        };
    }
}
