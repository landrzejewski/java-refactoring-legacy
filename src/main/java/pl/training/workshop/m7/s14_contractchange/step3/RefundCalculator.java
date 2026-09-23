package pl.training.workshop.m7.s14_contractchange.step3;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Krok 3 (rozwiązanie): świadoma decyzja zamiast "przy okazji".
 * - Format "0.00" to kontrakt (paragony, e-maile) - przywrócony: setScale PO max().
 * - Zaokrąglenie HALF_UP na BigDecimal to reguła domeny; różnica groszowa względem double
 *   została uzgodniona z księgowością i zatwierdzona jako ZMIANA KONTRAKTU w osobnym commicie
 *   (test S14ContractTest ma dla niej jawnie nowe oczekiwanie).
 */
public final class RefundCalculator {
    private static final BigDecimal FEE = new BigDecimal("3.00");

    public String refund(double ticketsPaid, long minutesBeforeStart) {
        BigDecimal refund = BigDecimal.valueOf(ticketsPaid)
                .multiply(share(minutesBeforeStart))
                .subtract(FEE)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
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
