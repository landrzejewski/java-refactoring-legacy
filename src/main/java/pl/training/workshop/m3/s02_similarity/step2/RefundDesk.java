package pl.training.workshop.m3.s02_similarity.step2;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Krok 2: Simplify - zostało to, co naprawdę mówi regulamin zwrotów: jedno potrącenie. */
public final class RefundDesk {
    public BigDecimal refund(BigDecimal paidForTickets, int percent) {
        BigDecimal share = paidForTickets.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return share.subtract(new BigDecimal("3.00")).max(BigDecimal.ZERO.setScale(2));
    }
}
