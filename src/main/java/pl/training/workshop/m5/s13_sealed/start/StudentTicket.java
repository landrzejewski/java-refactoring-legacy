package pl.training.workshop.m5.s13_sealed.start;

import pl.training.workshop.shared.Money;

/** Start: bilet studencki (25%). */
public record StudentTicket(Money basePrice) implements Ticket {
}
