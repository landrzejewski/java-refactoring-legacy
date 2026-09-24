import type { Decimal } from 'decimal.js';

/**
 * Start: "rozszerzalny" kontrakt reguły cenowej - priorytet, generyczny kontekst,
 * dowolne pluginy. Istnieją dokładnie dwie implementacje i nikt nie zgłosił trzeciej.
 */
export interface PricingRule {
  priority(): number;

  appliesTo(context: ReadonlyMap<string, unknown>): boolean;

  apply(context: ReadonlyMap<string, unknown>, price: Decimal): Decimal;
}
