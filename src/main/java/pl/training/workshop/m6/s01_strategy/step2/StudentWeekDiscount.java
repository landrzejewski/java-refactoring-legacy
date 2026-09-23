package pl.training.workshop.m6.s01_strategy.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: tydzień studenta - student 50%, pozostali jak w programie standardowym. */
public final class StudentWeekDiscount implements DiscountPolicy {
    private final DiscountPolicy fallback;

    public StudentWeekDiscount(DiscountPolicy fallback) {
        this.fallback = fallback;
    }

    @Override
    public Money discount(Money base, String ticketType) {
        return ticketType.equals("S") ? base.percent(50) : fallback.discount(base, ticketType);
    }
}
