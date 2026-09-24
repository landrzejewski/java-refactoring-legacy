import { Decimal } from 'decimal.js';

import { LocalTime } from '../../../shared/time.js';
import type { TicketRequest } from '../TicketRequest.js';

/**
 * Start: cała cena biletu w jednym wyrażeniu. Działa, ale żeby odpowiedzieć na pytanie
 * "skąd 32.00?", trzeba w głowie policzyć pięć zagnieżdżonych ternary.
 */
export class TicketPrice {
  price(r: TicketRequest): Decimal {
    return (r.format === 3 ? new Decimal('40.00')
      : r.format === 2 ? new Decimal('32.00') : new Decimal('25.00'))
      .times(100 - (r.type === 'S' ? 25
        : r.type === 'E' ? 30 : r.type === 'C' ? 40 : 0))
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP)
      .minus(r.start.isBefore(LocalTime.NOON)
        ? new Decimal('5.00') : new Decimal(0))
      .plus(r.row !== null && r.row >= 10 ? new Decimal('10.00') : new Decimal(0))
      .plus(r.format === 2 && !r.ownGlasses ? new Decimal('3.00') : new Decimal(0));
  }
}
