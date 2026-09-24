import { Decimal } from 'decimal.js';

/**
 * Start: kasa liczy rabat grupowy (10+ biletów = -10%). Ta sama reguła żyje też w WebShop -
 * inaczej zapisana i z innym zaokrągleniem. To duplikacja wiedzy, nie tylko tekstu.
 */
export class BoxOffice {
  total(ticketPrices: readonly Decimal[]): Decimal {
    let sum = new Decimal(0);
    for (const price of ticketPrices) {
      sum = sum.plus(price);
    }
    if (ticketPrices.length > 9) {
      const discount = sum.times(new Decimal('0.10')).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
      sum = sum.minus(discount);
    }
    return sum.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
