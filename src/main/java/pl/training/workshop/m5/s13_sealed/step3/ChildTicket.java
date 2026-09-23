package pl.training.workshop.m5.s13_sealed.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: NOWY typ biletu dziecięcego (40%) - kompilator wymusił jego obsługę w PriceCalculator. */
public record ChildTicket(Money basePrice) implements Ticket {
}
