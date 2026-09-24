import { Decimal } from 'decimal.js';

/**
 * Krok 1: ujednolicenie zapisu - ten sam kształt, te same nazwy i stałe co w BoxOffice.
 * Zachowanie bez zmian: jedyna różnica (HALF_EVEN) stoi teraz w jednej, widocznej linii.
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
      const discount = tickets.times(WebShop.GROUP_DISCOUNT).toDecimalPlaces(2, Decimal.ROUND_HALF_EVEN);
      tickets = tickets.minus(discount);
    }
    const fees = WebShop.FEE.times(ticketPrices.length);
    return tickets.plus(fees).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
