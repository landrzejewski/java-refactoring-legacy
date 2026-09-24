import type { Decimal } from 'decimal.js';

import { Pricing } from './Pricing.js';

/** Sprzedaż karnetu - format, poranek i okulary podane "na wszelki wypadek". */
export class PassCounter {
  private readonly pricing = new Pricing();

  pass(entries: number): Decimal {
    return this.pricing.price('2D', entries, true, false, true);
  }
}
