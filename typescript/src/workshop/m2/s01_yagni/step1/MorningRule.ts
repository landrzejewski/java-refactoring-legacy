import { Decimal } from 'decimal.js';

import type { LocalTime } from '../../../shared/time.js';
import type { PricingRule } from './PricingRule.js';

/** Seans poranny -5.00. */
export class MorningRule implements PricingRule {
  appliesTo(context: ReadonlyMap<string, unknown>): boolean {
    return (context.get('start') as LocalTime).hour < 12;
  }

  apply(_context: ReadonlyMap<string, unknown>, price: Decimal): Decimal {
    return price.minus(new Decimal('5.00'));
  }
}
