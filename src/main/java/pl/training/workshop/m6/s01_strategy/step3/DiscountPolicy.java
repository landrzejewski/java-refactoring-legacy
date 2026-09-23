package pl.training.workshop.m6.s01_strategy.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: kontrakt strategii - bez zmian. */
@FunctionalInterface
public interface DiscountPolicy {
    Money discount(Money base, String ticketType);
}
