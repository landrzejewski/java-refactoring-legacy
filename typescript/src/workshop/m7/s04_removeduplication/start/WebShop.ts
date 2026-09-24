import { Decimal } from 'decimal.js';

/**
 * Start: sklep internetowy - ta sama reguła rabatu grupowego co w BoxOffice, ale zapisana
 * inaczej (reduce, x*10/100) i zaokrąglana HALF_EVEN. Czy to celowa różnica, czy przypadek?
 */
export class WebShop {
  private static readonly FEE = new Decimal('2.00');

  total(prices: readonly Decimal[]): Decimal {
    let tickets = prices.reduce((sum, price) => sum.plus(price), new Decimal(0));
    if (prices.length >= 10) {
      tickets = tickets.minus(tickets.times(10)
        .dividedBy(new Decimal('100')).toDecimalPlaces(2, Decimal.ROUND_HALF_EVEN));
    }
    const fees = WebShop.FEE.times(prices.length);
    return tickets.plus(fees).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
