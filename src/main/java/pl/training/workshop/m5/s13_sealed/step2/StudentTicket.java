package pl.training.workshop.m5.s13_sealed.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: bilet studencki (25%). */
public record StudentTicket(Money basePrice) implements Ticket {
}
