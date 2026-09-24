import { Decimal } from 'decimal.js';

import type { TicketQuote } from '../TicketQuote.js';
import type { PricingRule } from './PricingRule.js';

/** Seans poranny -5.00. */
export class MorningRule implements PricingRule {
  appliesTo(quote: TicketQuote): boolean {
    return quote.start.hour < 12;
  }

  apply(price: Decimal): Decimal {
    return price.minus(new Decimal('5.00'));
  }
}
