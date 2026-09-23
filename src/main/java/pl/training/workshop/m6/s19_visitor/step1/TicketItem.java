package pl.training.workshop.m6.s19_visitor.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: bilet (VAT 8%) z accept. */
public record TicketItem(String title, String format, Money price) implements OrderItem {
    @Override
    public <R> R accept(OrderItemVisitor<R> visitor) {
        return visitor.visitTicket(this);
    }
}
