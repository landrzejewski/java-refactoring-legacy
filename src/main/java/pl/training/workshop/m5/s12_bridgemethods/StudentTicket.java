package pl.training.workshop.m5.s12_bridgemethods;

import pl.training.workshop.shared.Money;

/** Stabilny kontrakt sceny: bilet studencki. */
public record StudentTicket(Money basePrice, String studentId) implements Ticket {
}
