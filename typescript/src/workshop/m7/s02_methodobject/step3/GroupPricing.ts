import type { GroupOrder } from '../GroupOrder.js';
import type { Quote } from '../Quote.js';
import { GroupQuoteCalculation } from './GroupQuoteCalculation.js';

/**
 * Krok 3: fasada bez zmian - publiczne API i sposób tworzenia obiektu metody
 * (nowy na każde wywołanie) zostają takie jak w kroku 1. Zmienia się GroupQuoteCalculation.
 */
export class GroupPricing {
  quote(order: GroupOrder): Quote {
    return new GroupQuoteCalculation(order).calculate();
  }
}
