import { Decimal } from 'decimal.js';

import type { Order } from '../Order.js';

/**
 * Krok 2: pętla miała DWA wyjścia (subtotal i vipSeats), więc IDE
 * nie wydzieli jej w jedną metodę. Najpierw Split Loop, potem dwa
 * razy Extract Method - każda metoda ma jedno wyjście.
 */
export class TicketSummary {
  describe(order: Order): string {
    const base = this.basePrice(order);
    const subtotal = this.subtotal(order, base);
    const vipSeats = this.vipSeats(order);

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

  private basePrice(order: Order): Decimal {
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
    return base;
  }

  private subtotal(order: Order, base: Decimal): Decimal {
    let subtotal = new Decimal(0);
    for (const row of order.rows) {
      let price = base;
      if (row >= 10) {
        price = price.plus(new Decimal('10.00'));
      }
      subtotal = subtotal.plus(price);
    }
    return subtotal.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }

  private vipSeats(order: Order): number {
    let vipSeats = 0;
    for (const row of order.rows) {
      if (row >= 10) {
        vipSeats++;
      }
    }
    return vipSeats;
  }
}
