package pl.training.workshop.m6.s07_decorator.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: wąski kontrakt wspólny dla rdzenia i przyszłych dekoratorów. */
public interface PricedTicket {
    Money price();

    String description();
}
