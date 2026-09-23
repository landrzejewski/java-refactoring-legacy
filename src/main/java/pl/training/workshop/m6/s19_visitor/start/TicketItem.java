package pl.training.workshop.m6.s19_visitor.start;

import pl.training.workshop.shared.Money;

/** Start: bilet (VAT 8%). */
public record TicketItem(String title, String format, Money price) implements OrderItem {
}
