import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { BookingRequest } from '../BookingRequest.js';
import type { Outbox } from '../Outbox.js';
import { BookingValidator } from './BookingValidator.js';
import { TicketPricer } from './TicketPricer.js';

/** Krok 2: Extract Class dla wyceny - BookingDesk deleguje do TicketPricer. */
export class BookingDesk {
  private readonly outbox: Outbox;
  private readonly validator = new BookingValidator();
  private readonly pricer = new TicketPricer();

  constructor(outbox: Outbox) {
    this.outbox = requireNonNull(outbox, 'outbox');
  }

  book(request: BookingRequest): string {
    const error = this.validator.firstError(request);
    if (error !== undefined) {
      return error;
    }
    const pricing = this.pricer.price(request);

    // powiadomienie
    let text = `Rezerwacja ${request.seats.length} miejsc`;
    if (pricing.vipSeats > 0) {
      text = `${text} (VIP: ${pricing.vipSeats})`;
    }
    this.outbox.send(request.email, `${text}, do zaplaty ${pricing.total.toFixed(2)}`);
    return `OK ${pricing.total.toFixed(2)}`;
  }
}
