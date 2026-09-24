import type { Money } from '../../../shared/Money.js';

/** Krok 3: Extract Interface - klient zależy od kontraktu cennika, nie od singletona. */
export interface Tariff {
  basePrice(format: string): Money;
}
