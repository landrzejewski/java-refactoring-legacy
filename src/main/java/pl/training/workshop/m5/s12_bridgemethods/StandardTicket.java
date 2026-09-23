package pl.training.workshop.m5.s12_bridgemethods;

import pl.training.workshop.shared.Money;

/** Stabilny kontrakt sceny: bilet normalny. */
public record StandardTicket(Money basePrice) implements Ticket {
}
