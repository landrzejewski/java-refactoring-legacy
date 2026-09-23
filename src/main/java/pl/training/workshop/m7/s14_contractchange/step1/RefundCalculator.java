package pl.training.workshop.m7.s14_contractchange.step1;

import java.util.Locale;

/**
 * Krok 1: czysta refaktoryzacja - Extract Method share() dla progów czasowych.
 * Nadal double, więc arytmetyka i zaokrąglenie są bit w bit takie same (x * 1.0 == x).
 */
public final class RefundCalculator {
    public String refund(double ticketsPaid, long minutesBeforeStart) {
        double refund = ticketsPaid * share(minutesBeforeStart) - 3.00;
        if (refund < 0) {
            refund = 0;
        }
        refund = Math.round(refund * 100) / 100.0;
        return String.format(Locale.ROOT, "%.2f", refund);
    }

    private static double share(long minutesBeforeStart) {
        if (minutesBeforeStart <= 0) {
            return 0;
        }
        if (minutesBeforeStart >= 24 * 60) {
            return 1.0;
        }
        return 0.5;
    }
}
