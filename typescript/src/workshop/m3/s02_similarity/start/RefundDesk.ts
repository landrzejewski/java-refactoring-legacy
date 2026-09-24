import { Decimal } from 'decimal.js';

import { Kind, ServiceFee } from './ServiceFee.js';

/** Zwroty: procent zapłaconej kwoty minus potrącenie, nie mniej niż zero. */
export class RefundDesk {
  refund(paidForTickets: Decimal, percent: number): Decimal {
    const share = paidForTickets.times(percent)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    // "1", bo potrącenie jest za zwrot, a nie za bilet - parametr pasuje tylko drugiej regule
    return Decimal.max(share.minus(ServiceFee.of(Kind.REFUND, 1)), 0);
  }
}
