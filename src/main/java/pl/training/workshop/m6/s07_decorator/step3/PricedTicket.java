package pl.training.workshop.m6.s07_decorator.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: bez zmian - wąski kontrakt wspólny dla rdzenia i przyszłych dekoratorów. */
public interface PricedTicket {
    Money price();

    String description();
}
