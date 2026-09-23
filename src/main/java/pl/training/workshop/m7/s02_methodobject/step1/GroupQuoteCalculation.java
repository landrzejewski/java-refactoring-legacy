package pl.training.workshop.m7.s02_methodobject.step1;

import java.time.LocalTime;

import pl.training.workshop.m7.s02_methodobject.GroupOrder;
import pl.training.workshop.m7.s02_methodobject.Quote;
import pl.training.workshop.shared.Money;

/**
 * Krok 1: obiekt metody - jedno wykonanie wyceny. Nie jest usługą współdzieloną:
 * powstaje dla każdego wywołania. Ciało skopiowane 1:1 z GroupPricing.quote().
 */
final class GroupQuoteCalculation {
    private final GroupOrder order;

    GroupQuoteCalculation(GroupOrder order) {
        this.order = order;
    }

    Quote calculate() {
        Money base;
        switch (order.format()) {
            case "IMAX" -> base = Money.of("40.00");
            case "3D" -> base = Money.of("32.00");
            default -> base = Money.of("25.00");
        }
        boolean morning = order.start().isBefore(LocalTime.NOON);
        boolean glasses = order.format().equals("3D") && !order.ownGlasses();
        Money tickets = Money.ZERO;
        int count = 0;
        for (String type : order.ticketTypes()) {
            int discount = switch (type) {
                case "STUDENT" -> 25;
                case "SENIOR" -> 30;
                case "CHILD" -> 40;
                default -> 0;
            };
            Money price = base.minus(base.percent(discount));
            if (morning) {
                price = price.minus(Money.of("5.00"));
            }
            if (glasses) {
                price = price.plus(Money.of("3.00"));
            }
            tickets = tickets.plus(price);
            count++;
        }
        tickets = tickets.plus(Money.of("10.00").times(order.vipSeats()));
        if (count >= 10) {
            tickets = tickets.minus(tickets.percent(10));
        }
        Money fees = order.online() ? Money.of("2.00").times(count) : Money.ZERO;
        Money total = tickets.plus(fees);
        int points = tickets.amount().intValue() / 10;
        return new Quote(tickets, fees, total, points);
    }
}
