package pl.training.workshop.m6.s01_strategy.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: kontrakt strategii - wysokość zniżki dla ceny bazowej i typu biletu. */
@FunctionalInterface
public interface DiscountPolicy {
    Money discount(Money base, String ticketType);
}
