import type { GroupOrder } from '../GroupOrder.js';
import type { Quote } from '../Quote.js';
import { GroupQuoteCalculation } from './GroupQuoteCalculation.js';

/**
 * Krok 1: Extract Method Object - ciało metody skopiowane bez upraszczania
 * do GroupQuoteCalculation. Publiczna metoda zostaje jako fasada i tylko deleguje.
 */
export class GroupPricing {
  quote(order: GroupOrder): Quote {
    return new GroupQuoteCalculation(order).calculate();
  }
}
