package pl.training.workshop.m5.s13_sealed.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: bilet studencki (25%). */
public record StudentTicket(Money basePrice) implements Ticket {
}
