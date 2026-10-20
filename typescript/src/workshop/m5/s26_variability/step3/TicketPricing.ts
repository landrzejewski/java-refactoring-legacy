import type { Money } from '../../../shared/Money.js';

/**
 * Krok 3: Strategy (bez zmian od kroku 1) - wymienny algorytm ceny jednego biletu. Zna tylko cenę bazową formatu:
 * nic o rezerwacji, jej statusie ani o operacji kasjera.
 * (W Javie: @FunctionalInterface; w TypeScripcie interfejs z jedną metodą.)
 */
export interface TicketPricing {
  price(formatBase: Money): Money;
}
