import { Decimal } from 'decimal.js';

import { Kind } from './ServiceFee.js';

/**
 * Krok 1: Inline Method - wspólna metoda wróciła do obu wywołujących.
 * Tymczasowe powtórzenie kodu to bezpieczny etap rozdzielania reguł.
 */
export class OnlineCheckout {
  total(ticketPrices: readonly Decimal[]): Decimal {
    const tickets = ticketPrices.reduce((sum, price) => sum.plus(price), new Decimal(0));
    // "as Kind" - wstawiony argument; bez rzutowania kompilator sam zauważy stały warunek
    const perUnit = (Kind.ONLINE_BOOKING as Kind) === Kind.ONLINE_BOOKING
      ? new Decimal('2.00') : new Decimal('3.00');
    return tickets.plus(perUnit.times(ticketPrices.length)
      .toDecimalPlaces(2, Decimal.ROUND_HALF_UP));
  }
}
