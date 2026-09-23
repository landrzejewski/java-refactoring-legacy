package pl.training.workshop.m6.s20_decisionmap.start;

import java.time.DayOfWeek;

import pl.training.workshop.shared.Money;

/**
 * Start: tabela cen wpisana w zagnieżdżone warunki - format x dzień tygodnia. Dwie osie
 * zmienności (format i reguła dnia: "tani wtorek" -30%, weekend +2.00) są splecione.
 */
public final class ShowPricing {
    public Money price(DayOfWeek day, String format) {
        boolean weekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
        if (format.equals("2D")) {
            return Money.of(day == DayOfWeek.TUESDAY ? "17.50" : weekend ? "27.00" : "25.00");
        } else if (format.equals("3D")) {
            return Money.of(day == DayOfWeek.TUESDAY ? "22.40" : weekend ? "34.00" : "32.00");
        } else if (format.equals("IMAX")) {
            return Money.of(day == DayOfWeek.TUESDAY ? "28.00" : weekend ? "42.00" : "40.00");
        }
        throw new IllegalArgumentException("unknown format: " + format);
    }
}
