import { Decimal } from 'decimal.js';

import type { TicketQuote } from '../TicketQuote.js';
import { RuleRegistry } from './RuleRegistry.js';

/**
 * Start: spekulatywny silnik reguł cenowych (YAGNI). Rejestr pluginów, konfiguracja
 * napisem, priorytety i kontekst Map<string, unknown> - wszystko dla DWÓCH reguł,
 * które nie zależą od kolejności. Literówka w konfiguracji wybucha dopiero w runtime.
 */
export class TicketPricer {
  constructor(
    private readonly registry: RuleRegistry = RuleRegistry.withDefaults(),
    private readonly activeRules: string = 'morning,vip',
  ) {}

  price(quote: TicketQuote): Decimal {
    const context = new Map<string, unknown>();
    context.set('format', quote.format);
    context.set('start', quote.start);
    context.set('row', quote.row);
    context.set('vipFromRow', quote.vipFromRow);
    let price = this.basePrice(quote.format);
    for (const rule of this.registry.resolve(this.activeRules)) {
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
