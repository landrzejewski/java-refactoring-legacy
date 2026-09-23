package pl.training.workshop.m6.s01_strategy.step3;

import java.util.Objects;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: kontekst dostaje strategię w konstruktorze i nie zna nazw programów.
 * Uwaga: wybór w konstruktorze zamraża decyzję - zmienia moment, w którym pada błąd nieznanego programu.
 */
public final class TicketPricer {
    private final DiscountPolicy policy;

    public TicketPricer(DiscountPolicy policy) {
        this.policy = Objects.requireNonNull(policy, "policy");
    }

    public Money price(Money base, String ticketType) {
        if (base.compareTo(Money.ZERO) < 0) {
            throw new IllegalArgumentException("base price must not be negative");
        }
        return base.minus(policy.discount(base, ticketType));
    }
}
