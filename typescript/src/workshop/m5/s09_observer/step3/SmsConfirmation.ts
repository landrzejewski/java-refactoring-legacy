import type { SmsGateway } from '../SmsGateway.js';
import type { PaymentListener } from './PaymentListener.js';
import type { ReservationPaid } from './ReservationPaid.js';

/** Krok 3: obserwator - potwierdzenie SMS. */
export class SmsConfirmation implements PaymentListener {
  constructor(readonly sms: SmsGateway) {}

  onPaid(event: ReservationPaid): void {
    this.sms.send(event.phone, `Oplacono ${event.reservationId}`);
  }
}
