package pl.training.workshop.m5.s26_variability.step3;

import pl.training.workshop.shared.Money;

/**
 * Krok 3: Strategy (bez zmian od kroku 1) - wymienny algorytm ceny jednego biletu. Zna tylko cenę bazową formatu:
 * nic o rezerwacji, jej statusie ani o operacji kasjera.
 */
@FunctionalInterface
public interface TicketPricing {
    Money price(Money formatBase);
}
