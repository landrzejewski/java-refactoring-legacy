import { Decimal } from 'decimal.js';

import { LocalTime } from '../../../shared/time.js';
import type { TicketRequest } from '../TicketRequest.js';

/**
 * Krok 2: Extract Variable dla warunków - morning, vipSeat, needsGlasses.
 * vipSeat wydzielamy RAZEM z osłoną `r.row !== null &&`: krótkie spięcie jest zachowaniem.
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
    const morning = r.start.isBefore(LocalTime.NOON);
    const vipSeat = r.row !== null && r.row >= 10;
    const needsGlasses = r.format === 2 && !r.ownGlasses;
    return discountedPrice
      .minus(morning ? new Decimal('5.00') : new Decimal(0))
      .plus(vipSeat ? new Decimal('10.00') : new Decimal(0))
      .plus(needsGlasses ? new Decimal('3.00') : new Decimal(0));
  }
}
