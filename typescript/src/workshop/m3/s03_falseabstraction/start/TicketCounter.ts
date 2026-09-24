import type { Decimal } from 'decimal.js';

import { Pricing } from './Pricing.js';

/** Sprzedaż pojedynczego biletu - "quantity" i "pass" nic tu nie znaczą. */
export class TicketCounter {
  private readonly pricing = new Pricing();

  ticket(format: string, morning: boolean, ownGlasses: boolean): Decimal {
    return this.pricing.price(format, 1, false, morning, ownGlasses);
  }
}
