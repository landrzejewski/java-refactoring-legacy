import { Decimal } from 'decimal.js';

import type { PricingRule } from './PricingRule.js';

/** Miejsce VIP +10.00. */
export class VipRule implements PricingRule {
  appliesTo(context: ReadonlyMap<string, unknown>): boolean {
    return (context.get('row') as number) >= (context.get('vipFromRow') as number);
  }

  apply(_context: ReadonlyMap<string, unknown>, price: Decimal): Decimal {
    return price.plus(new Decimal('10.00'));
  }
}
