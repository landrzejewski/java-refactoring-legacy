package pl.training.workshop.m3.s02_similarity.start;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Zwroty: procent zapłaconej kwoty minus potrącenie, nie mniej niż zero. */
public final class RefundDesk {
    public BigDecimal refund(BigDecimal paidForTickets, int percent) {
        BigDecimal share = paidForTickets.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        // "1", bo potrącenie jest za zwrot, a nie za bilet - parametr pasuje tylko drugiej regule
        return share.subtract(ServiceFee.of(ServiceFee.Kind.REFUND, 1)).max(BigDecimal.ZERO.setScale(2));
    }
}
