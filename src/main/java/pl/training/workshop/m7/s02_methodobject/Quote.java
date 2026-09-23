package pl.training.workshop.m7.s02_methodobject;

import pl.training.workshop.shared.Money;

/** Stabilny kontrakt sceny: wycena zamówienia. */
public record Quote(Money tickets, Money fees, Money total, int loyaltyPoints) {
}
