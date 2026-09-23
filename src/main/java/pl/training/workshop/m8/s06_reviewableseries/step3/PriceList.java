package pl.training.workshop.m8.s06_reviewableseries.step3;

import java.time.DayOfWeek;

import pl.training.workshop.m8.s06_reviewableseries.TicketQuery;
import pl.training.workshop.shared.Money;

/**
 * Krok 3 (commit 3, zmiana zachowania): nowa reguła "Tani wtorek" - NORMAL -20% ceny bazowej
 * we wtorek. Diff to kilka linii, więc recenzent widzi wyłącznie nową regułę biznesową.
 */
public final class PriceList {
    private static final int CHEAP_TUESDAY_PERCENT = 20;

    public Money price(TicketQuery query) {
        Money base = basePrice(query.format());
        Money price = base.minus(base.percent(discountPercent(query)));
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

    private static int discountPercent(TicketQuery query) {
        return switch (query.type()) {
            case "STUDENT" -> 25;
            case "SENIOR" -> 30;
            case "CHILD" -> 40;
            default -> isCheapTuesday(query) ? CHEAP_TUESDAY_PERCENT : 0;
        };
    }

    private static boolean isCheapTuesday(TicketQuery query) {
        return query.start().getDayOfWeek() == DayOfWeek.TUESDAY;
    }
}
