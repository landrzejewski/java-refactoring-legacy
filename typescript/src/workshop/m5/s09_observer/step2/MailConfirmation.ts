import type { Mailer } from '../Mailer.js';
import type { PaymentListener } from './PaymentListener.js';
import type { ReservationPaid } from './ReservationPaid.js';

/** Krok 2: obserwator - potwierdzenie mailem. */
export class MailConfirmation implements PaymentListener {
  constructor(readonly mailer: Mailer) {}

  onPaid(event: ReservationPaid): void {
    this.mailer.send(event.email,
      `Potwierdzenie platnosci ${event.reservationId}: ${event.amount.toString()}`);
  }
}
