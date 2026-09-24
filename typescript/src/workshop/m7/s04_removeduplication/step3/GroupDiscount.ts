import { Decimal } from 'decimal.js';

/**
 * Krok 3: Extract Class - reguła "10+ biletów = -10%, rabat zaokrąglany HALF_UP" ma
 * jednego właściciela. Opłata rezerwacyjna nie jest częścią reguły i zostaje w WebShop.
 * Eksportowana tylko na potrzeby kasy i sklepu (w Javie klasa pakietowa).
 */
export class GroupDiscount {
  private static readonly GROUP_SIZE = 10;
  private static readonly GROUP_DISCOUNT = new Decimal('0.10');

  private constructor() {}

  static ticketsTotal(ticketPrices: readonly Decimal[]): Decimal {
    let tickets = new Decimal(0);
    for (const price of ticketPrices) {
      tickets = tickets.plus(price);
    }
    if (ticketPrices.length >= GroupDiscount.GROUP_SIZE) {
      const discount = tickets.times(GroupDiscount.GROUP_DISCOUNT).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
      tickets = tickets.minus(discount);
    }
    return tickets;
  }
}
