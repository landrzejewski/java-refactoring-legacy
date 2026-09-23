package pl.training.workshop.m5.s13_sealed.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: bilet normalny (0%). */
public record StandardTicket(Money basePrice) implements Ticket {
}
