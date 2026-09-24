import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { BookingRequest } from '../BookingRequest.js';
import type { Outbox } from '../Outbox.js';
import { BookingNotifier } from './BookingNotifier.js';
import { BookingValidator } from './BookingValidator.js';
import { TicketPricer } from './TicketPricer.js';

/**
 * Krok 3 (rozwiązanie): BookingDesk tylko składa trzy kroki - waliduj, wyceń, powiadom.
 * Publiczne API (konstruktor z Outbox, book) bez zmian; szczegóły mają osobnych właścicieli.
 * (Java ma dodatkowo pakietowy konstruktor z trzema zależnościami; TS nie ma przeciążeń
 * konstruktorów, więc części są składane w jedynym konstruktorze.)
 */
export class BookingDesk {
  private readonly validator: BookingValidator;
  private readonly pricer: TicketPricer;
  private readonly notifier: BookingNotifier;

  constructor(outbox: Outbox) {
    this.validator = new BookingValidator();
    this.pricer = new TicketPricer();
    this.notifier = new BookingNotifier(requireNonNull(outbox, 'outbox'));
  }

  book(request: BookingRequest): string {
    const error = this.validator.firstError(request);
    if (error !== undefined) {
      return error;
    }
    const pricing = this.pricer.price(request);
    this.notifier.bookingConfirmed(request, pricing);
    return `OK ${pricing.total.toFixed(2)}`;
  }
}
