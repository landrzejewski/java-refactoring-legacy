package pl.training.workshop.m5.s04_encapsulatefactory.start.tickets;

import pl.training.workshop.shared.Money;

/** Wspólny kontrakt biletu - jedyny typ, który klient naprawdę potrzebuje znać. */
public interface Ticket {
    Money price();

    String describe();
}
