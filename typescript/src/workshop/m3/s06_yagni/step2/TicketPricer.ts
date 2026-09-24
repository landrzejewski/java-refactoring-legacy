import { Decimal } from 'decimal.js';

import type { TicketQuote } from '../TicketQuote.js';
import { MorningRule } from './MorningRule.js';
import type { PricingRule } from './PricingRule.js';
import { VipRule } from './VipRule.js';

/**
 * Krok 2: Change Signature - reguły dostają `TicketQuote` zamiast generycznej
 * mapy "kontekstu". Znika budowanie mapy i rzutowania; kompilator pilnuje nazw pól.
 */
export class TicketPricer {
  private readonly rules: readonly PricingRule[] = Object.freeze([new MorningRule(), new VipRule()]);

  price(quote: TicketQuote): Decimal {
    let price = this.basePrice(quote.format);
    for (const rule of this.rules) {
      if (rule.appliesTo(quote)) {
        price = rule.apply(price);
      }
    }
    return price.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }

  private basePrice(format: string): Decimal {
    switch (format) {
      case 'IMAX': return new Decimal('40.00');
      case '3D': return new Decimal('32.00');
      default: return new Decimal('25.00');
    }
  }
}
