import type { Decimal } from 'decimal.js';

import type { TicketQuote } from '../TicketQuote.js';

/** Krok 2: kontrakt na typowanych danych - bez rzutowań z Map<string, unknown>. */
export interface PricingRule {
  appliesTo(quote: TicketQuote): boolean;

  apply(price: Decimal): Decimal;
}
