import { Decimal } from 'decimal.js';

/** Krok 2: Simplify - zostało to, co naprawdę mówi regulamin zwrotów: jedno potrącenie. */
export class RefundDesk {
  refund(paidForTickets: Decimal, percent: number): Decimal {
    const share = paidForTickets.times(percent)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    return Decimal.max(share.minus(new Decimal('3.00')), 0);
  }
}
