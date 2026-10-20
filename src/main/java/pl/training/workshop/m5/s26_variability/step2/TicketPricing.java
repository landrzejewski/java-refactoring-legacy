package pl.training.workshop.m5.s26_variability.step2;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: Strategy (bez zmian) - wymienny algorytm ceny jednego biletu. Zna tylko cenę bazową formatu:
 * nic o rezerwacji, jej statusie ani o operacji kasjera.
 */
@FunctionalInterface
public interface TicketPricing {
    Money price(Money formatBase);
}
