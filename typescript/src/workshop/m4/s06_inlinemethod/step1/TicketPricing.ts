import { Decimal } from 'decimal.js';

import { LocalTime } from '../../../shared/time.js';
import type { Ticket } from '../Ticket.js';

const MORNING_REDUCTION = new Decimal('5.00');

/**
 * Krok 1: Inline Method `base` - prywatny, niepolimorficzny delegat z jednym wywołaniem.
 * Nie dodawał znaczenia ponad basePrice, więc to najbezpieczniejszy możliwy Inline.
 */
export class TicketPricing {
  total(ticket: Ticket): Decimal {
    return this.addFee(this.price(ticket));
  }

  price(ticket: Ticket): Decimal {
    const price = basePrice(ticket.format);
    return ticket.start.isBefore(LocalTime.NOON) ? price.minus(MORNING_REDUCTION) : price;
  }

  /** Kasa nie pobiera opłaty rezerwacyjnej. Nadpisywane w OnlineTicketPricing. */
  protected bookingFee(): Decimal {
    return new Decimal('0.00');
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
