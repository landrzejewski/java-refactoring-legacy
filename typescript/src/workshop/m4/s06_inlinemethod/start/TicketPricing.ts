import { Decimal } from 'decimal.js';

import { LocalTime } from '../../../shared/time.js';
import type { Ticket } from '../Ticket.js';

const MORNING_REDUCTION = new Decimal('5.00');

/**
 * Start: cennik kasy. Dwa pośredniki bez znaczenia (base, addFee) i jeden hak
 * nadpisywany w OnlineTicketPricing (bookingFee). Wszystkie trzy "wyglądają" na trywialne.
 */
export class TicketPricing {
  total(ticket: Ticket): Decimal {
    return this.addFee(this.price(ticket));
  }

  price(ticket: Ticket): Decimal {
    const price = this.base(ticket);
    return ticket.start.isBefore(LocalTime.NOON) ? price.minus(MORNING_REDUCTION) : price;
  }

  /** Kasa nie pobiera opłaty rezerwacyjnej. Nadpisywane w OnlineTicketPricing. */
  protected bookingFee(): Decimal {
    return new Decimal('0.00');
  }

  private base(ticket: Ticket): Decimal {
    return basePrice(ticket.format);
  }

  private addFee(price: Decimal): Decimal {
    return price.plus(this.bookingFee());
  }
}

function basePrice(format: number): Decimal {
  switch (format) {
    case 3: return new Decimal('40.00');
    case 2: return new Decimal('32.00');
    default: return new Decimal('25.00');
  }
}
