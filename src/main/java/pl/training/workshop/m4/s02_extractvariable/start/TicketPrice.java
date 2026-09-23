package pl.training.workshop.m4.s02_extractvariable.start;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;

import pl.training.workshop.m4.s02_extractvariable.TicketRequest;

/**
 * Start: cała cena biletu w jednym wyrażeniu. Działa, ale żeby odpowiedzieć na pytanie
 * "skąd 32.00?", trzeba w głowie policzyć pięć zagnieżdżonych ternary.
 */
public final class TicketPrice {
    public BigDecimal price(TicketRequest r) {
        return (r.format() == 3 ? new BigDecimal("40.00")
                        : r.format() == 2 ? new BigDecimal("32.00") : new BigDecimal("25.00"))
                .multiply(BigDecimal.valueOf(100 - (r.type().equals("S") ? 25
                        : r.type().equals("E") ? 30 : r.type().equals("C") ? 40 : 0)))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .subtract(r.start().isBefore(LocalTime.NOON)
                        ? new BigDecimal("5.00") : BigDecimal.ZERO)
                .add(r.row() != null && r.row() >= 10 ? new BigDecimal("10.00") : BigDecimal.ZERO)
                .add(r.format() == 2 && !r.ownGlasses() ? new BigDecimal("3.00") : BigDecimal.ZERO);
    }
}
