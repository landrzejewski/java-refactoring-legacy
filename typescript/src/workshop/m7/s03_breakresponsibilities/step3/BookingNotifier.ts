import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { BookingRequest } from '../BookingRequest.js';
import type { Outbox } from '../Outbox.js';
import type { Pricing } from './Pricing.js';

/** Krok 3: Extract Class - treść i wysyłka powiadomienia mają jednego właściciela. */
export class BookingNotifier {
  private readonly outbox: Outbox;

  constructor(outbox: Outbox) {
    this.outbox = requireNonNull(outbox, 'outbox');
  }

  bookingConfirmed(request: BookingRequest, pricing: Pricing): void {
    let text = `Rezerwacja ${request.seats.length} miejsc`;
    if (pricing.vipSeats > 0) {
      text = `${text} (VIP: ${pricing.vipSeats})`;
    }
    this.outbox.send(request.email, `${text}, do zaplaty ${pricing.total.toFixed(2)}`);
  }
}
