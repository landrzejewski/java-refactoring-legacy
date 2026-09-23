package pl.training.workshop.m3.s02_similarity.step3;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Krok 3 (rozwiązanie): potrącenie przy zwrocie to reguła regulaminu zwrotów
 * (właściciel: obsługa klienta, prawnik). Własna stała, własny powód zmiany.
 */
public final class RefundDesk {
    private static final BigDecimal REFUND_DEDUCTION = new BigDecimal("3.00");

    public BigDecimal refund(BigDecimal paidForTickets, int percent) {
        BigDecimal share = paidForTickets.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return share.subtract(REFUND_DEDUCTION).max(BigDecimal.ZERO.setScale(2));
    }
}
