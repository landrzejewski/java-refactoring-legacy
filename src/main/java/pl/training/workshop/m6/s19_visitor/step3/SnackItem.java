package pl.training.workshop.m6.s19_visitor.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: produkt baru (VAT 23%) - czyste dane, bez accept. */
public record SnackItem(String name, Money price) implements OrderItem {
}
