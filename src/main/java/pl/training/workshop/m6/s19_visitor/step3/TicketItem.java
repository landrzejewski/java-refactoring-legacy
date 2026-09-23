package pl.training.workshop.m6.s19_visitor.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: bilet (VAT 8%) - czyste dane, bez accept. */
public record TicketItem(String title, String format, Money price) implements OrderItem {
}
