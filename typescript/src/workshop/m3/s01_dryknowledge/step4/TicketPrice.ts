import { Decimal } from 'decimal.js';

import type { Ticket } from '../Ticket.js';

/**
 * Krok 4: jedyna, autorytatywna reprezentacja wiedzy "ile kosztuje bilet".
 * Właściciel: dział cennika. Sprzedaż i zwrot tylko z niej korzystają.
 */
export class TicketPrice {
  private static readonly MORNING_DISCOUNT = new Decimal('5.00');

  of(ticket: Ticket): Decimal {
    const base = this.basePrice(ticket.format);
    let price = base.minus(base.times(this.discountPercent(ticket.type))
      .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP));
    if (ticket.start.hour < 12) {
      price = price.minus(TicketPrice.MORNING_DISCOUNT);
    }
    return price.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }

  private basePrice(format: string): Decimal {
    switch (format) {
      case 'IMAX': return new Decimal('40.00');
      case '3D': return new Decimal('32.00');
      default: return new Decimal('25.00');
    }
  }

  private discountPercent(type: string): number {
    switch (type) {
      case 'STUDENT': return 25;
      case 'SENIOR': return 30;
      case 'CHILD': return 40;
      default: return 0;
    }
  }
}
