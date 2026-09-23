package pl.training.workshop.m5.s13_sealed.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: bilet seniora (30%). */
public record SeniorTicket(Money basePrice) implements Ticket {
}
