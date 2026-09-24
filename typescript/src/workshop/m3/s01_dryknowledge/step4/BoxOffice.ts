import { Decimal } from 'decimal.js';

import type { Ticket } from '../Ticket.js';
import { TicketPrice } from './TicketPrice.js';

/**
 * Krok 4 (rozwiązanie): Extract Class - reguła ceny przeniesiona do `TicketPrice`.
 * BoxOffice zna już tylko własną wiedzę: reguły zwrotu. Zmiana zniżki = jedno miejsce.
 */
export class BoxOffice {
  private static readonly REFUND_DEDUCTION = new Decimal('3.00');

  private readonly ticketPrice = new TicketPrice();

  sell(ticket: Ticket): Decimal {
    return this.ticketPrice.of(ticket);
  }

  refund(ticket: Ticket, hoursBeforeStart: number): Decimal {
    const paid = this.ticketPrice.of(ticket);
    const percent = hoursBeforeStart >= 24 ? 100 : hoursBeforeStart > 0 ? 50 : 0;
    const refund = paid.times(percent)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP)
      .minus(BoxOffice.REFUND_DEDUCTION);
    return Decimal.max(refund, 0).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
