import type { Decimal } from 'decimal.js';

import type { Screening } from './Screening.js';

/**
 * Krok 1: po Extract Class zostaje sama polityka cenowa (LCOM4 = 1)
 * i żadnego importu technologii.
 */
export class ScreeningService {
  constructor(private readonly basePrice: Decimal, private readonly morningDiscount: Decimal) {}

  price(screening: Screening): Decimal {
    return this.isMorning(screening) ? this.basePrice.minus(this.morningDiscount) : this.basePrice;
  }

  private isMorning(screening: Screening): boolean {
    return screening.start.hour < 12;
  }
}
