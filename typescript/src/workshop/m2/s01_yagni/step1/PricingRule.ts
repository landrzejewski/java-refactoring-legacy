import type { Decimal } from 'decimal.js';

/** Krok 1: bez priorytetu - kolejność wyznacza lista w TicketPricer. */
export interface PricingRule {
  appliesTo(context: ReadonlyMap<string, unknown>): boolean;

  apply(context: ReadonlyMap<string, unknown>, price: Decimal): Decimal;
}
