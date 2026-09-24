import type { Money } from '../../../shared/Money.js';

/**
 * Krok 3: Extract Interface dla integracji - rola, którą kontener DI może opakować proxy albo
 * dowolnym obiektem delegującym, bez dziedziczenia po implementacji i bez dotykania jej pól #.
 */
export interface Pricing {
  studentPrice(basePrice: Money): Money;
}
