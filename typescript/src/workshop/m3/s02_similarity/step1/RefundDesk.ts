import { Decimal } from 'decimal.js';

import { Kind } from './ServiceFee.js';

/** Krok 1: Inline Method - kopia wspólnej logiki, jeszcze z przełącznikiem. */
export class RefundDesk {
  refund(paidForTickets: Decimal, percent: number): Decimal {
    const share = paidForTickets.times(percent)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    // "as Kind" - wstawiony argument; bez rzutowania kompilator sam zauważy stały warunek
    const perUnit = (Kind.REFUND as Kind) === Kind.ONLINE_BOOKING
      ? new Decimal('2.00') : new Decimal('3.00');
    const fee = perUnit.times(1).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    return Decimal.max(share.minus(fee), 0);
  }
}
