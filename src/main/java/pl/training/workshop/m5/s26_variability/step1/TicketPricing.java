package pl.training.workshop.m5.s26_variability.step1;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: Strategy - wymienny algorytm ceny jednego biletu. Zna tylko cenę bazową formatu:
 * nic o rezerwacji, jej statusie ani o operacji kasjera.
 */
@FunctionalInterface
public interface TicketPricing {
    Money price(Money formatBase);
}
