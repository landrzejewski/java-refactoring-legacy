import { Decimal } from 'decimal.js';

import type { Order } from '../Order.js';

export class TicketSummary {
  describe(order: Order): string {
    // cena bazowa formatu
    let base: Decimal;
    if (order.format === 'IMAX') {
      base = new Decimal('40.00');
    } else if (order.format === '3D') {
      base = new Decimal('32.00');
    } else {
      base = new Decimal('25.00');
    }
    if (order.start.hour < 12) {
      base = base.minus(new Decimal('5.00'));
    }

    // suma i liczba miejsc VIP
    let subtotal = new Decimal(0);
    let vipSeats = 0;
    for (const row of order.rows) {
      let price = base;
      if (row >= 10) {
        price = price.plus(new Decimal('10.00'));
        vipSeats++;
      }
      subtotal = subtotal.plus(price);
    }
    subtotal = subtotal.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);

    // dokument
    let text = '';
    text += 'BILETY: ' + order.title + '\n';
    text += 'Format: ' + order.format
      + ', start ' + order.start.toString() + '\n';
    text += 'Miejsc: ' + order.rows.length;
    if (vipSeats > 0) {
      text += ' (w tym VIP: ' + vipSeats + ')';
    }
    text += '\n';
    text += 'Razem: ' + subtotal.toFixed(2) + '\n';
    return text;
  }
}
