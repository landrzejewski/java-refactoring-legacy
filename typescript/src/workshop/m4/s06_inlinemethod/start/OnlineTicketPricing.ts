import { Decimal } from 'decimal.js';

import { TicketPricing } from './TicketPricing.js';

/** Sprzedaż internetowa: ta sama cena biletu, plus opłata rezerwacyjna 2.00. */
export class OnlineTicketPricing extends TicketPricing {
  protected override bookingFee(): Decimal {
    return new Decimal('2.00');
  }
}
