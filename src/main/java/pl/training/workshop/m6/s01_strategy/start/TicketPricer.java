package pl.training.workshop.m6.s01_strategy.start;

import pl.training.workshop.shared.Money;

/**
 * Start: algorytm zniżki wybierany łańcuchem if po nazwie programu. Każdy nowy program
 * (np. "tydzień studenta") dopisuje gałąź w środku metody, która zawiera też walidację.
 */
public final class TicketPricer {
    public Money price(Money base, String ticketType, String program) {
        if (base.compareTo(Money.ZERO) < 0) {
            throw new IllegalArgumentException("base price must not be negative");
        }
        if (program == null) {
            throw new IllegalArgumentException("program must not be null");
        }
        Money discount;
        if (program.equals("PREMIERE")) {
            discount = Money.ZERO;
        } else if (program.equals("STUDENT_WEEK") && ticketType.equals("S")) {
            discount = base.percent(50);
        } else if (program.equals("STANDARD") || program.equals("STUDENT_WEEK")) {
            int percent = switch (ticketType) {
                case "N" -> 0;
                case "S" -> 25;
                case "E" -> 30;
                case "C" -> 40;
                default -> throw new IllegalArgumentException("unknown ticket type: " + ticketType);
            };
            discount = base.percent(percent);
        } else {
            throw new IllegalArgumentException("unknown program: " + program);
        }
        return base.minus(discount);
    }
}
