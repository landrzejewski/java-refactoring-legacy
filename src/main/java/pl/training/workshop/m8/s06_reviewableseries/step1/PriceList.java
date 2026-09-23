package pl.training.workshop.m8.s06_reviewableseries.step1;

import pl.training.workshop.m8.s06_reviewableseries.TicketQuery;
import pl.training.workshop.shared.Money;

/**
 * Krok 1 (commit 1, refaktoryzacja): Extract Method basePrice i discountPercent, Money zamiast
 * BigDecimal. Zachowanie bez zmian - dowodem jest test równoważności, nie opis w commicie.
 */
public final class PriceList {
    public Money price(TicketQuery query) {
        Money base = basePrice(query.format());
        Money price = base.minus(base.percent(discountPercent(query.type())));
        if (query.start().getHour() < 12) {
            price = price.minus(Money.of("5.00"));
        }
        if (query.row() >= 10) {
            price = price.plus(Money.of("10.00"));
        }
        return price;
    }

    private static Money basePrice(String format) {
        return switch (format) {
            case "IMAX" -> Money.of("40.00");
            case "3D" -> Money.of("32.00");
            default -> Money.of("25.00");
        };
    }

    private static int discountPercent(String type) {
        return switch (type) {
            case "STUDENT" -> 25;
            case "SENIOR" -> 30;
            case "CHILD" -> 40;
            default -> 0;
        };
    }
}
