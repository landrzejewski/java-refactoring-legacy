import type { Money } from '../../../shared/Money.js';

/** Krok 1: kontrakt strategii - wysokość zniżki dla ceny bazowej i typu biletu. */
export interface DiscountPolicy {
  discount(base: Money, ticketType: string): Money;
}
