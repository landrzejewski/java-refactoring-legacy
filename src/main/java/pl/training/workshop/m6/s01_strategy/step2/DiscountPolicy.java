package pl.training.workshop.m6.s01_strategy.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: kontrakt strategii - bez zmian względem kroku 1. */
@FunctionalInterface
public interface DiscountPolicy {
    Money discount(Money base, String ticketType);
}
