package pl.training.workshop.m6.s04_encapsulatefactory.step3.tickets;

import pl.training.workshop.shared.Money;

/** Krok 3: publiczny kontrakt biletu - bez zmian. */
public interface Ticket {
    Money price();

    String describe();
}
