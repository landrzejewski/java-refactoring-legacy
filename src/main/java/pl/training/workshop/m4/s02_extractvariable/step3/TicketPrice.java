package pl.training.workshop.m4.s02_extractvariable.step3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;

import pl.training.workshop.m4.s02_extractvariable.TicketRequest;

/**
 * Krok 3 (rozwiązanie): Extract Variable dla kwot dopłat i obniżek.
 * Ostatnia instrukcja czyta się jak paragon: cena po zniżce - poranek + VIP + okulary.
 */
public final class TicketPrice {
    public BigDecimal price(TicketRequest r) {
        BigDecimal basePrice = r.format() == 3 ? new BigDecimal("40.00")
                : r.format() == 2 ? new BigDecimal("32.00") : new BigDecimal("25.00");
        int discountPercent = r.type().equals("S") ? 25
                : r.type().equals("E") ? 30 : r.type().equals("C") ? 40 : 0;
        BigDecimal discountedPrice = basePrice
                .multiply(BigDecimal.valueOf(100 - discountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        boolean morning = r.start().isBefore(LocalTime.NOON);
        boolean vipSeat = r.row() != null && r.row() >= 10;
        boolean needsGlasses = r.format() == 2 && !r.ownGlasses();
        BigDecimal morningReduction = morning ? new BigDecimal("5.00") : BigDecimal.ZERO;
        BigDecimal vipSurcharge = vipSeat ? new BigDecimal("10.00") : BigDecimal.ZERO;
        BigDecimal glassesFee = needsGlasses ? new BigDecimal("3.00") : BigDecimal.ZERO;
        return discountedPrice.subtract(morningReduction).add(vipSurcharge).add(glassesFee);
    }
}
