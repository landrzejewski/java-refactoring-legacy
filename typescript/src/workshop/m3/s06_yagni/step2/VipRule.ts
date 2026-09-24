import { Decimal } from 'decimal.js';

import type { TicketQuote } from '../TicketQuote.js';
import type { PricingRule } from './PricingRule.js';

/** Miejsce VIP +10.00. */
export class VipRule implements PricingRule {
  appliesTo(quote: TicketQuote): boolean {
    return quote.row >= quote.vipFromRow;
  }

  apply(price: Decimal): Decimal {
    return price.plus(new Decimal('10.00'));
  }
}
