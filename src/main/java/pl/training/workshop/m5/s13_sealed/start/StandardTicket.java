package pl.training.workshop.m5.s13_sealed.start;

import pl.training.workshop.shared.Money;

/** Start: bilet normalny (0%). */
public record StandardTicket(Money basePrice) implements Ticket {
}
