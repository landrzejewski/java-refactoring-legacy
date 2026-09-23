package pl.training.workshop.m6.s19_visitor.step1;

/** Krok 1: element przyjmuje odwiedzającego (double dispatch). */
public interface OrderItem {
    <R> R accept(OrderItemVisitor<R> visitor);
}
