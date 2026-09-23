package pl.training.workshop.m8.s07_adr.step2.pricing;

import pl.training.workshop.shared.Money;

/** Krok 2: kwota w Money (R2). */
public record Quote(Money total, boolean groupDiscount) {
}
