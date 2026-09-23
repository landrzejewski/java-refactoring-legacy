package pl.training.workshop.m6.s01_strategy.step2;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: gałęzie przeniesione do strategii (po jednej, test po każdej). W kontekście zostaje
 * wspólna walidacja i jeden switch wybierający strategię - wciąż przy każdym wywołaniu.
 */
public final class TicketPricer {
    public Money price(Money base, String ticketType, String program) {
        if (base.compareTo(Money.ZERO) < 0) {
            throw new IllegalArgumentException("base price must not be negative");
        }
        if (program == null) {
            throw new IllegalArgumentException("program must not be null");
        }
        return base.minus(policyFor(program).discount(base, ticketType));
    }

    private static DiscountPolicy policyFor(String program) {
        return switch (program) {
            case "STANDARD" -> new StandardDiscount();
            case "STUDENT_WEEK" -> new StudentWeekDiscount(new StandardDiscount());
            case "PREMIERE" -> new PremiereDiscount();
            default -> throw new IllegalArgumentException("unknown program: " + program);
        };
    }
}
