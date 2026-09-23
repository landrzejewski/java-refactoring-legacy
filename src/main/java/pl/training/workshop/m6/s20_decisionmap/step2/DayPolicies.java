package pl.training.workshop.m6.s20_decisionmap.step2;

import java.time.DayOfWeek;

import pl.training.workshop.shared.Money;

/**
 * Krok 2 (ścieżka A): kalendarz polityk. Gdy marketing co miesiąc dodaje akcje ("środa
 * seniora", "noc kina"), zmienia się tylko ten kalendarz albo powstaje nowy.
 */
public final class DayPolicies {
    public static final DayPolicy CHEAP_TUESDAY = base -> base.minus(base.percent(30));
    public static final DayPolicy WEEKEND = base -> base.plus(Money.of("2.00"));
    public static final DayPolicy REGULAR = base -> base;

    private DayPolicies() {
    }

    public static DayPolicy standard(DayOfWeek day) {
        return switch (day) {
            case TUESDAY -> CHEAP_TUESDAY;
            case SATURDAY, SUNDAY -> WEEKEND;
            default -> REGULAR;
        };
    }
}
