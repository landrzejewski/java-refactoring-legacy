package pl.training.workshop.m7.s06_parameterobject.step2;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Krok 2: Move Method - opis terminu przeniesiony do ScreeningSlot.label(); stare sygnatury
 * nadal delegują (okres migracji). Walidacja nadal tu, bez zmian.
 */
public final class ScreeningPlanner {
    /** @deprecated użyj {@link #describe(ScreeningSlot)} */
    @Deprecated
    public String describe(String screeningId, LocalDate date, int hall, String format) {
        return describe(new ScreeningSlot(screeningId, date, hall, format));
    }

    /** @deprecated użyj {@link #ticketPrice(ScreeningSlot)} */
    @Deprecated
    public BigDecimal ticketPrice(String screeningId, LocalDate date, int hall, String format) {
        return ticketPrice(new ScreeningSlot(screeningId, date, hall, format));
    }

    public String describe(ScreeningSlot slot) {
        return slot.label();
    }

    public BigDecimal ticketPrice(ScreeningSlot slot) {
        if (slot.hall() < 1 || slot.hall() > 8) {
            throw new IllegalArgumentException(
                    "nie ma sali " + slot.hall() + " (" + slot.screeningId() + ")");
        }
        return switch (slot.format()) {
            case "2D" -> new BigDecimal("25.00");
            case "3D" -> new BigDecimal("32.00");
            case "IMAX" -> new BigDecimal("40.00");
            default -> throw new IllegalArgumentException(
                    "nieznany format " + slot.format() + " (" + slot.screeningId() + ")");
        };
    }
}
