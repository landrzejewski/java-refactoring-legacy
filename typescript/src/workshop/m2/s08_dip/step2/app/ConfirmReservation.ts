import { IllegalArgumentError, IllegalStateError } from '../../../../../shared/errors.js';
import type { Reservation } from '../../Reservation.js';
import type { SmtpMailSender } from '../infra/SmtpMailSender.js';

/**
 * Krok 2: Extract Method - potrzeba polityki dostaje nazwę w języku problemu:
 * `notifyCustomer(email, message)`. Cały "technologiczny" kod (MIME, kody SMTP)
 * trafił do tej jednej metody. Tak wygląda port, zanim stanie się interfejsem.
 */
export class ConfirmReservation {
  constructor(private readonly mail: SmtpMailSender) {}

  confirm(reservation: Reservation): string {
    if (reservation.seats < 1) {
      throw new IllegalArgumentError('rezerwacja bez miejsc');
    }
    const message = `Rezerwacja: ${reservation.title}, ${reservation.start.toString()}`
      + `, miejsc: ${reservation.seats}. Zaplac w ciagu 15 minut.`;
    this.notifyCustomer(reservation.email, message);
    return `potwierdzono: ${reservation.email}`;
  }

  private notifyCustomer(email: string, message: string): void {
    const mime = `To: ${email}\r\nSubject: Rezerwacja\r\n\r\n${message}`;
    const reply = this.mail.send(email, mime);
    if (!reply.startsWith('250')) {
      throw new IllegalStateError(`SMTP odrzucil: ${reply}`);
    }
  }
}
