package pl.training.workshop.m6.s01_strategy.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: premiera - brak zniżek (typ biletu nie jest nawet sprawdzany, jak w start). */
public final class PremiereDiscount implements DiscountPolicy {
    @Override
    public Money discount(Money base, String ticketType) {
        return Money.ZERO;
    }
}
