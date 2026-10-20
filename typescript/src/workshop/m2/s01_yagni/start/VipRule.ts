import { Decimal } from 'decimal.js';

import type { PricingRule } from './PricingRule.js';

/** Start: plugin "miejsce VIP +10.00". */
export class VipRule implements PricingRule {
  priority(): number {
    return 20;
  }

  appliesTo(context: ReadonlyMap<string, unknown>): boolean {
    return (context.get('row') as number) >= (context.get('vipFromRow') as number);
  }

  apply(_context: ReadonlyMap<string, unknown>, price: Decimal): Decimal {
    return price.plus(new Decimal('10.00'));
  }
}
