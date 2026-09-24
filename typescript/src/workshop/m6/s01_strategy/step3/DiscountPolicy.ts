import type { Money } from '../../../shared/Money.js';

/** Krok 3: kontrakt strategii - bez zmian. */
export interface DiscountPolicy {
  discount(base: Money, ticketType: string): Money;
}
