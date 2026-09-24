import { Decimal } from 'decimal.js';

import { GroupDiscount } from './GroupDiscount.js';

/** Krok 3 (rozwiązanie): kasa korzysta ze wspólnej reguły GroupDiscount. */
export class BoxOffice {
  total(ticketPrices: readonly Decimal[]): Decimal {
    return GroupDiscount.ticketsTotal(ticketPrices).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
