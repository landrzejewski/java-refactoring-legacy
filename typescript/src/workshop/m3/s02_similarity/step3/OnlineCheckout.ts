import { Decimal } from 'decimal.js';

/**
 * Krok 3 (rozwiązanie): Extract Constant z nazwą w języku domeny, u właściciela reguły.
 * Opłata rezerwacyjna należy do sprzedaży online i zmienia się z jej powodów
 * (promocje, konkurencja). Nie ma nic wspólnego z potrąceniem przy zwrocie.
 */
export class OnlineCheckout {
  private static readonly BOOKING_FEE_PER_TICKET = new Decimal('2.00');

  total(ticketPrices: readonly Decimal[]): Decimal {
    const tickets = ticketPrices.reduce((sum, price) => sum.plus(price), new Decimal(0));
    return tickets.plus(OnlineCheckout.BOOKING_FEE_PER_TICKET.times(ticketPrices.length));
  }
}
