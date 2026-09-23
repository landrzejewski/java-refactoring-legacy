package pl.training.workshop.m3.s02_similarity.step1;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Krok 1: Inline Method - kopia wspólnej logiki, jeszcze z przełącznikiem. */
public final class RefundDesk {
    public BigDecimal refund(BigDecimal paidForTickets, int percent) {
        BigDecimal share = paidForTickets.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal perUnit = ServiceFee.Kind.REFUND == ServiceFee.Kind.ONLINE_BOOKING
                ? new BigDecimal("2.00") : new BigDecimal("3.00");
        BigDecimal fee = perUnit.multiply(BigDecimal.valueOf(1)).setScale(2, RoundingMode.HALF_UP);
        return share.subtract(fee).max(BigDecimal.ZERO.setScale(2));
    }
}
