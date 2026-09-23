package pl.training.workshop.m6.s01_strategy.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: gałąź STANDARD przeniesiona do strategii. Bezstanowa - można ją współdzielić. */
public final class StandardDiscount implements DiscountPolicy {
    @Override
    public Money discount(Money base, String ticketType) {
        int percent = switch (ticketType) {
            case "N" -> 0;
            case "S" -> 25;
            case "E" -> 30;
            case "C" -> 40;
            default -> throw new IllegalArgumentException("unknown ticket type: " + ticketType);
        };
        return base.percent(percent);
    }
}
