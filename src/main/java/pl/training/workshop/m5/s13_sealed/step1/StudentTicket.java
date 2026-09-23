package pl.training.workshop.m5.s13_sealed.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: bilet studencki (25%). */
public record StudentTicket(Money basePrice) implements Ticket {
}
