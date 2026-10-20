import type { Money } from '../../../shared/Money.js';

/** Krok 2: kontrakt strategii - bez zmian względem kroku 1. */
export interface DiscountPolicy {
  discount(base: Money, ticketType: string): Money;
}
