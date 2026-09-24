import { Decimal } from 'decimal.js';

/**
 * Krok 2: Simplify - warunek na stałej zawsze prawdziwy, martwa gałąź usunięta.
 * ServiceFee usunięty (Safe Delete), bo nikt go już nie używa.
 */
export class OnlineCheckout {
  total(ticketPrices: readonly Decimal[]): Decimal {
    const tickets = ticketPrices.reduce((sum, price) => sum.plus(price), new Decimal(0));
    return tickets.plus(new Decimal('2.00').times(ticketPrices.length));
  }
}
