package pl.training.workshop.m4.s02_extractvariable.step1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;

import pl.training.workshop.m4.s02_extractvariable.TicketRequest;

/**
 * Krok 1: Extract Variable dla ceny bazowej, procentu zniżki i ceny po zniżce.
 * Każda nazwa odpowiada pojęciu z cennika, a nie fragmentowi składni.
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
        return discountedPrice
                .subtract(r.start().isBefore(LocalTime.NOON)
                        ? new BigDecimal("5.00") : BigDecimal.ZERO)
                .add(r.row() != null && r.row() >= 10 ? new BigDecimal("10.00") : BigDecimal.ZERO)
                .add(r.format() == 2 && !r.ownGlasses() ? new BigDecimal("3.00") : BigDecimal.ZERO);
    }
}
