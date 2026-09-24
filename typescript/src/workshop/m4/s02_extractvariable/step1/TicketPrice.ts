import { Decimal } from 'decimal.js';

import { LocalTime } from '../../../shared/time.js';
import type { TicketRequest } from '../TicketRequest.js';

/**
 * Krok 1: Extract Variable dla ceny bazowej, procentu zniżki i ceny po zniżce.
 * Każda nazwa odpowiada pojęciu z cennika, a nie fragmentowi składni.
 */
export class TicketPrice {
  price(r: TicketRequest): Decimal {
    const basePrice = r.format === 3 ? new Decimal('40.00')
      : r.format === 2 ? new Decimal('32.00') : new Decimal('25.00');
    const discountPercent = r.type === 'S' ? 25
      : r.type === 'E' ? 30 : r.type === 'C' ? 40 : 0;
    const discountedPrice = basePrice
      .times(100 - discountPercent)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    return discountedPrice
      .minus(r.start.isBefore(LocalTime.NOON)
        ? new Decimal('5.00') : new Decimal(0))
      .plus(r.row !== null && r.row >= 10 ? new Decimal('10.00') : new Decimal(0))
      .plus(r.format === 2 && !r.ownGlasses ? new Decimal('3.00') : new Decimal(0));
  }
}
