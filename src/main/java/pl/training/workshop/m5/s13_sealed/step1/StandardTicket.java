package pl.training.workshop.m5.s13_sealed.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: bilet normalny (0%). */
public record StandardTicket(Money basePrice) implements Ticket {
}
