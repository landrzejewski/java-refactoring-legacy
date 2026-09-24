import { Decimal } from 'decimal.js';

import { GroupDiscount } from './GroupDiscount.js';

/**
 * Krok 3 (rozwiązanie): sklep korzysta ze wspólnej reguły GroupDiscount; różnica
 * względem kasy (opłata 2.00 za bilet) zostaje jawna, w tej klasie.
 */
export class WebShop {
  private static readonly FEE = new Decimal('2.00');

  total(ticketPrices: readonly Decimal[]): Decimal {
    const fees = WebShop.FEE.times(ticketPrices.length);
    return GroupDiscount.ticketsTotal(ticketPrices).plus(fees).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
