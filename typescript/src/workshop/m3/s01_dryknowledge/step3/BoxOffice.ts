import { Decimal } from 'decimal.js';

import type { Ticket } from '../Ticket.js';

/**
 * Krok 3: Substitute Algorithm - zwrot korzysta z `ticketPrice`,
 * kopia `paidFor` usunięta (Safe Delete). Test równoważności potwierdza,
 * że obie kopie reguły dawały te same kwoty dla wszystkich przypadków.
 */
export class BoxOffice {
  sell(ticket: Ticket): Decimal {
    return this.ticketPrice(ticket);
  }

  refund(ticket: Ticket, hoursBeforeStart: number): Decimal {
    const paid = this.ticketPrice(ticket);
    // zwrot: >= 24h 100%, < 24h 50%, po starcie 0%; potrącenie 3.00
    const percent = hoursBeforeStart >= 24 ? 100 : hoursBeforeStart > 0 ? 50 : 0;
    const refund = paid.times(percent)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP)
      .minus(new Decimal('3.00'));
    return Decimal.max(refund, 0).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }

  private ticketPrice(ticket: Ticket): Decimal {
    let base: Decimal;
    switch (ticket.format) {
      case 'IMAX': base = new Decimal('40.00'); break;
      case '3D': base = new Decimal('32.00'); break;
      default: base = new Decimal('25.00');
    }
    let discount: number;
    switch (ticket.type) {
      case 'STUDENT': discount = 25; break;
      case 'SENIOR': discount = 30; break;
      case 'CHILD': discount = 40; break;
      default: discount = 0;
    }
    let price = base.minus(base.times(discount)
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP));
    if (ticket.start.hour < 12) {
      price = price.minus(new Decimal('5.00'));
    }
    return price.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
