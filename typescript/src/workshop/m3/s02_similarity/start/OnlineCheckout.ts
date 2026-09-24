import { Decimal } from 'decimal.js';

import { Kind, ServiceFee } from './ServiceFee.js';

/** Sprzedaż online: suma biletów plus opłata rezerwacyjna za każdy bilet. */
export class OnlineCheckout {
  total(ticketPrices: readonly Decimal[]): Decimal {
    const tickets = ticketPrices.reduce((sum, price) => sum.plus(price), new Decimal(0));
    return tickets.plus(ServiceFee.of(Kind.ONLINE_BOOKING, ticketPrices.length));
  }
}
