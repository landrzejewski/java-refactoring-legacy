package pl.training.workshop.m7.s02_methodobject.step3;

import java.time.LocalTime;

import pl.training.workshop.m7.s02_methodobject.GroupOrder;
import pl.training.workshop.m7.s02_methodobject.Quote;
import pl.training.workshop.shared.Money;

/**
 * Krok 3 (rozwiązanie): Extract Method wewnątrz obiektu metody. Pola niosą stan
 * między krokami, więc każdy blok stał się metodą bez parametrów, a calculate()
 * czyta się jak spis treści. Kolejność obliczeń bez zmian.
 */
final class GroupQuoteCalculation {
    private final GroupOrder order;
    private Money base;
    private boolean morning;
    private boolean glasses;
    private Money tickets = Money.ZERO;
    private int count;

    GroupQuoteCalculation(GroupOrder order) {
        this.order = order;
    }

    Quote calculate() {
        readConditions();
        addTickets();
        addVipSeats();
        applyGroupDiscount();
        Money fees = bookingFees();
        return new Quote(tickets, fees, tickets.plus(fees), loyaltyPoints());
    }

    private void readConditions() {
        base = switch (order.format()) {
            case "IMAX" -> Money.of("40.00");
            case "3D" -> Money.of("32.00");
            default -> Money.of("25.00");
        };
        morning = order.start().isBefore(LocalTime.NOON);
        glasses = order.format().equals("3D") && !order.ownGlasses();
    }

    private void addTickets() {
        for (String type : order.ticketTypes()) {
            tickets = tickets.plus(ticketPrice(type));
            count++;
        }
    }

    private Money ticketPrice(String type) {
        Money price = base.minus(base.percent(discountPercent(type)));
        if (morning) {
            price = price.minus(Money.of("5.00"));
        }
        if (glasses) {
            price = price.plus(Money.of("3.00"));
        }
        return price;
    }

    private static int discountPercent(String type) {
        return switch (type) {
            case "STUDENT" -> 25;
            case "SENIOR" -> 30;
            case "CHILD" -> 40;
            default -> 0;
        };
    }

    private void addVipSeats() {
        tickets = tickets.plus(Money.of("10.00").times(order.vipSeats()));
    }

    private void applyGroupDiscount() {
        if (count >= 10) {
            tickets = tickets.minus(tickets.percent(10));
        }
    }

    private Money bookingFees() {
        return order.online() ? Money.of("2.00").times(count) : Money.ZERO;
    }

    private int loyaltyPoints() {
        return tickets.amount().intValue() / 10;
    }
}
