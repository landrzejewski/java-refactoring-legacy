package pl.training.workshop.m6.s20_decisionmap.step2;

import java.time.DayOfWeek;
import java.util.Objects;
import java.util.function.Function;

import pl.training.workshop.shared.Money;

/**
 * Krok 2 (ścieżka A - zmienia się reguła dnia): Replace Conditional Logic with Strategy.
 * Kalendarz polityk jest wstrzykiwany; format pozostaje prostym switchem, bo jest stabilny.
 */
public final class ShowPricing {
    private final Function<DayOfWeek, DayPolicy> calendar;

    public ShowPricing() {
        this(DayPolicies::standard);
    }

    public ShowPricing(Function<DayOfWeek, DayPolicy> calendar) {
        this.calendar = Objects.requireNonNull(calendar, "calendar");
    }

    public Money price(DayOfWeek day, String format) {
        return calendar.apply(day).apply(basePrice(format));
    }

    private static Money basePrice(String format) {
        return switch (format) {
            case "2D" -> Money.of("25.00");
            case "3D" -> Money.of("32.00");
            case "IMAX" -> Money.of("40.00");
            default -> throw new IllegalArgumentException("unknown format: " + format);
        };
    }
}
