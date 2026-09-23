package pl.training.workshop.m6.s01_strategy.step1;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: Extract Interface + strategia przejściowa. Kontekst woła już DiscountPolicy,
 * ale jedyna implementacja to lambda delegująca do starego łańcucha if. Mały, odwracalny ruch.
 */
public final class TicketPricer {
    public Money price(Money base, String ticketType, String program) {
        if (base.compareTo(Money.ZERO) < 0) {
            throw new IllegalArgumentException("base price must not be negative");
        }
        if (program == null) {
            throw new IllegalArgumentException("program must not be null");
        }
        DiscountPolicy policy = (b, type) -> legacyDiscount(b, type, program);
        return base.minus(policy.discount(base, ticketType));
    }

    private static Money legacyDiscount(Money base, String ticketType, String program) {
        if (program.equals("PREMIERE")) {
            return Money.ZERO;
        } else if (program.equals("STUDENT_WEEK") && ticketType.equals("S")) {
            return base.percent(50);
        } else if (program.equals("STANDARD") || program.equals("STUDENT_WEEK")) {
            int percent = switch (ticketType) {
                case "N" -> 0;
                case "S" -> 25;
                case "E" -> 30;
                case "C" -> 40;
                default -> throw new IllegalArgumentException("unknown ticket type: " + ticketType);
            };
            return base.percent(percent);
        }
        throw new IllegalArgumentException("unknown program: " + program);
    }
}
