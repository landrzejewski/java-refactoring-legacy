package pl.training.workshop.m7.s06_parameterobject.step3;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Krok 3 (rozwiązanie): planner nie waliduje - poprawność gwarantuje ScreeningSlot.
 * Stare sygnatury nadal delegują, ale teraz rzucają już przy budowie ScreeningSlot.
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
        return switch (slot.format()) {
            case "2D" -> new BigDecimal("25.00");
            case "3D" -> new BigDecimal("32.00");
            default -> new BigDecimal("40.00");
        };
    }
}
