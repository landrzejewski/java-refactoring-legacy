package pl.training.workshop.m8.s06_reviewableseries.start;

import java.math.BigDecimal;

import pl.training.workshop.m8.s06_reviewableseries.TicketQuery;
import pl.training.workshop.shared.Money;

/**
 * Start: cennik, do którego trzeba dodać promocję "Tani wtorek" (NORMAL -20% we wtorek).
 * Zniżki są wplecione w jedną metodę i zależą tylko od typu biletu - nowa reguła
 * potrzebuje daty. Pokusa: jeden duży commit "porządki + tani wtorek".
 */
public final class PriceList {
    public Money price(TicketQuery q) {
        BigDecimal p;
        if (q.format().equals("IMAX")) {
            p = new BigDecimal("40.00");
        } else if (q.format().equals("3D")) {
            p = new BigDecimal("32.00");
        } else {
            p = new BigDecimal("25.00");
        }
        BigDecimal d;
        if (q.type().equals("STUDENT")) {
            d = p.multiply(new BigDecimal("0.25"));
        } else if (q.type().equals("SENIOR")) {
            d = p.multiply(new BigDecimal("0.30"));
        } else if (q.type().equals("CHILD")) {
            d = p.multiply(new BigDecimal("0.40"));
        } else {
            d = BigDecimal.ZERO;
        }
        p = p.subtract(d);
        if (q.start().getHour() < 12) {
            p = p.subtract(new BigDecimal("5.00"));
        }
        if (q.row() >= 10) {
            p = p.add(new BigDecimal("10.00"));
        }
        return new Money(p);
    }
}
