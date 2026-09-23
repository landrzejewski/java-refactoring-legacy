package pl.training.workshop.m7.s04_removeduplication.start;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Start: sklep internetowy - ta sama reguła rabatu grupowego co w BoxOffice, ale zapisana
 * inaczej (stream, x*10/100) i zaokrąglana HALF_EVEN. Czy to celowa różnica, czy przypadek?
 */
public final class WebShop {
    private static final BigDecimal FEE = new BigDecimal("2.00");

    public BigDecimal total(List<BigDecimal> prices) {
        BigDecimal tickets = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (prices.size() >= 10) {
            tickets = tickets.subtract(tickets.multiply(BigDecimal.TEN)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_EVEN));
        }
        BigDecimal fees = FEE.multiply(BigDecimal.valueOf(prices.size()));
        return tickets.add(fees).setScale(2, RoundingMode.HALF_UP);
    }
}
