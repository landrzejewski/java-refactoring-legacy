package pl.training.workshop.m5.s12_bridgemethods;

import pl.training.workshop.shared.Money;

/** Stabilny kontrakt sceny: bilet z ceną bazową formatu. */
public interface Ticket {
    Money basePrice();
}
