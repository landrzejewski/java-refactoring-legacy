import { Decimal } from 'decimal.js';

import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { BookingRequest } from '../BookingRequest.js';
import type { Outbox } from '../Outbox.js';
import { BookingValidator } from './BookingValidator.js';

/**
 * Krok 1: Extract Class dla walidacji - BookingDesk deleguje do BookingValidator.
 */
export class BookingDesk {
  private readonly outbox: Outbox;
  private readonly validator = new BookingValidator();

  constructor(outbox: Outbox) {
    this.outbox = requireNonNull(outbox, 'outbox');
  }

  book(request: BookingRequest): string {
    const error = this.validator.firstError(request);
    if (error !== undefined) {
      return error;
    }

    // wycena
    let base: Decimal;
    switch (request.format) {
      case 'IMAX': base = new Decimal('40.00'); break;
      case '3D': base = new Decimal('32.00'); break;
      default: base = new Decimal('25.00');
    }
    let total = new Decimal(0);
    let vipSeats = 0;
    for (const seat of request.seats) {
      total = total.plus(base);
      if (Number.parseInt(seat.substring(1), 10) >= 10) {
        total = total.plus(new Decimal('10.00'));
        vipSeats++;
      }
    }

    // powiadomienie
    let text = `Rezerwacja ${request.seats.length} miejsc`;
    if (vipSeats > 0) {
      text = `${text} (VIP: ${vipSeats})`;
    }
    this.outbox.send(request.email, `${text}, do zaplaty ${total.toFixed(2)}`);
    return `OK ${total.toFixed(2)}`;
  }
}
