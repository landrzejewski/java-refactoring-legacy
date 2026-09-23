package pl.training.workshop.m6.s19_visitor.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: produkt baru (VAT 23%) z accept. */
public record SnackItem(String name, Money price) implements OrderItem {
    @Override
    public <R> R accept(OrderItemVisitor<R> visitor) {
        return visitor.visitSnack(this);
    }
}
