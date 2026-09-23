package pl.training.workshop.m5.s13_sealed.start;

import pl.training.workshop.shared.Money;

/** Start: bilet seniora (30%). */
public record SeniorTicket(Money basePrice) implements Ticket {
}
