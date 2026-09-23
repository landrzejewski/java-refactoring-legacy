package pl.training.workshop.m5.s13_sealed.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: bilet seniora (30%). */
public record SeniorTicket(Money basePrice) implements Ticket {
}
