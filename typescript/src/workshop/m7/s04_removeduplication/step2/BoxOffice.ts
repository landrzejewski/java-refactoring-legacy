import { Decimal } from 'decimal.js';

/**
 * Krok 2: kasa bez zmian - zmienia się tylko WebShop (tryb zaokrąglenia rabatu).
 */
export class BoxOffice {
  private static readonly GROUP_SIZE = 10;
  private static readonly GROUP_DISCOUNT = new Decimal('0.10');

  total(ticketPrices: readonly Decimal[]): Decimal {
    let tickets = new Decimal(0);
    for (const price of ticketPrices) {
      tickets = tickets.plus(price);
    }
    if (ticketPrices.length >= BoxOffice.GROUP_SIZE) {
      const discount = tickets.times(BoxOffice.GROUP_DISCOUNT).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
      tickets = tickets.minus(discount);
    }
    return tickets.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
