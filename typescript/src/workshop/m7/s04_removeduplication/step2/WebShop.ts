import { Decimal } from 'decimal.js';

/**
 * Krok 2: świadoma decyzja (zmiana kontraktu, osobny commit) - biznes potwierdził,
 * że rabat zaokrąglamy HALF_UP jak w kasie. Dla rabatu z końcówką 5 na trzecim miejscu
 * po przecinku wynik online się zmienia.
 */
export class WebShop {
  private static readonly GROUP_SIZE = 10;
  private static readonly GROUP_DISCOUNT = new Decimal('0.10');
  private static readonly FEE = new Decimal('2.00');

  total(ticketPrices: readonly Decimal[]): Decimal {
    let tickets = new Decimal(0);
    for (const price of ticketPrices) {
      tickets = tickets.plus(price);
    }
    if (ticketPrices.length >= WebShop.GROUP_SIZE) {
      const discount = tickets.times(WebShop.GROUP_DISCOUNT).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
      tickets = tickets.minus(discount);
    }
    const fees = WebShop.FEE.times(ticketPrices.length);
    return tickets.plus(fees).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
