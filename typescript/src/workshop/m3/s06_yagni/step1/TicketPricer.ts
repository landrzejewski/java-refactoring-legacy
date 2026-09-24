import { Decimal } from 'decimal.js';

import type { TicketQuote } from '../TicketQuote.js';
import { MorningRule } from './MorningRule.js';
import type { PricingRule } from './PricingRule.js';
import { VipRule } from './VipRule.js';

/**
 * Krok 1: Inline Class - rejestr pluginów i konfiguracja napisem znikają.
 * Reguły to zwykła lista tworzona przez new; priorytet (Safe Delete) był potrzebny
 * tylko do sortowania w rejestrze. Literówka w nazwie reguły już się nie skompiluje.
 */
export class TicketPricer {
  private readonly rules: readonly PricingRule[] = Object.freeze([new MorningRule(), new VipRule()]);

  price(quote: TicketQuote): Decimal {
    const context = new Map<string, unknown>();
    context.set('format', quote.format);
    context.set('start', quote.start);
    context.set('row', quote.row);
    context.set('vipFromRow', quote.vipFromRow);
    let price = this.basePrice(quote.format);
    for (const rule of this.rules) {
      if (rule.appliesTo(context)) {
        price = rule.apply(context, price);
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
