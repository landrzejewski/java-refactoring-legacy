import { Decimal } from 'decimal.js';

import { LocalTime } from '../../../shared/time.js';
import type { Ticket } from '../Ticket.js';

/**
 * Krok 2: Extract Method po stronie sprzedaży - `ticketPrice`.
 * Teraz obie kopie reguły stoją obok siebie jako metody o jednym wejściu i wyjściu.
 */
export class BoxOffice {
  sell(ticket: Ticket): Decimal {
    return this.ticketPrice(ticket);
  }

  refund(ticket: Ticket, hoursBeforeStart: number): Decimal {
    const paid = this.paidFor(ticket);
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

  private paidFor(ticket: Ticket): Decimal {
    let paid = new Decimal('25.00');
    if (ticket.format === '3D') {
      paid = new Decimal('32.00');
    } else if (ticket.format === 'IMAX') {
      paid = new Decimal('40.00');
    }
    if (ticket.type === 'STUDENT') {
      paid = paid.times(new Decimal('0.75'));
    } else if (ticket.type === 'SENIOR') {
      paid = paid.times(new Decimal('0.70'));
    } else if (ticket.type === 'CHILD') {
      paid = paid.times(new Decimal('0.60'));
    }
    if (ticket.start.isBefore(LocalTime.NOON)) {
      paid = paid.minus(new Decimal('5.00'));
    }
    return paid;
  }
}
