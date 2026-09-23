package pl.training.workshop.m6.s20_decisionmap.step1;

import java.time.DayOfWeek;

import pl.training.workshop.shared.Money;

/**
 * Krok 1 (wspólny dla obu ścieżek): rozplecenie osi. Tabela 3x3 to w rzeczywistości dwie
 * niezależne reguły: cena bazowa formatu i korekta dnia. Extract Method dla każdej osi.
 */
public final class ShowPricing {
    public Money price(DayOfWeek day, String format) {
        return adjustForDay(day, basePrice(format));
    }

    private static Money basePrice(String format) {
        return switch (format) {
            case "2D" -> Money.of("25.00");
            case "3D" -> Money.of("32.00");
            case "IMAX" -> Money.of("40.00");
            default -> throw new IllegalArgumentException("unknown format: " + format);
        };
    }

    private static Money adjustForDay(DayOfWeek day, Money base) {
        return switch (day) {
            case TUESDAY -> base.minus(base.percent(30));
            case SATURDAY, SUNDAY -> base.plus(Money.of("2.00"));
            default -> base;
        };
    }
}
