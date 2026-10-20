package pl.training.workshop.m5.s19_visitor.start;

import pl.training.workshop.shared.Money;

/** Start: produkt baru (VAT 23%). */
public record SnackItem(String name, Money price) implements OrderItem {
}
