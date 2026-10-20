package pl.training.workshop.m5.s04_encapsulatefactory.step2.tickets;

import pl.training.workshop.shared.Money;

/** Krok 2: bez zmian. Wspólny kontrakt biletu - jedyny typ, który klient naprawdę potrzebuje znać. */
public interface Ticket {
    Money price();

    String describe();
}
