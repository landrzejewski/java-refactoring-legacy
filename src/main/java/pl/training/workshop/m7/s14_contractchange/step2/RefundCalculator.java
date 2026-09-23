package pl.training.workshop.m7.s14_contractchange.step2;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Krok 2: "przy okazji" - w jednym ruchu double -> BigDecimal. Wygląda na porządki,
 * ale zmienia DWIE rzeczy w kontrakcie: zaokrąglenie połówek (29.175 -> 29.18 zamiast 29.17)
 * i format zera (BigDecimal.ZERO drukuje się jako "0", a nie "0.00"). Test to wykrywa.
 */
public final class RefundCalculator {
    private static final BigDecimal FEE = new BigDecimal("3.00");

    public String refund(double ticketsPaid, long minutesBeforeStart) {
        BigDecimal refund = BigDecimal.valueOf(ticketsPaid)
                .multiply(share(minutesBeforeStart))
                .subtract(FEE)
                .setScale(2, RoundingMode.HALF_UP)
                .max(BigDecimal.ZERO);
        return refund.toPlainString();
    }

    private static BigDecimal share(long minutesBeforeStart) {
        if (minutesBeforeStart <= 0) {
            return BigDecimal.ZERO;
        }
        if (minutesBeforeStart >= 24 * 60) {
            return BigDecimal.ONE;
        }
        return new BigDecimal("0.5");
    }
}
