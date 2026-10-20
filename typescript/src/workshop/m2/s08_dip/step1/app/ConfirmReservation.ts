import { IllegalArgumentError, IllegalStateError } from '../../../../../shared/errors.js';
import type { Reservation } from '../../Reservation.js';
import type { SmtpMailSender } from '../infra/SmtpMailSender.js';

/**
 * Krok 1: Introduce Parameter - klient SMTP wstrzyknięty przez konstruktor.
 * To jest dependency injection, ale jeszcze NIE DIP: import wciąż prowadzi
 * z polityki do szczegółu (app -> infra), a polityka zna MIME i kody SMTP.
 */
export class ConfirmReservation {
  constructor(private readonly mail: SmtpMailSender) {}

  confirm(reservation: Reservation): string {
    if (reservation.seats < 1) {
      throw new IllegalArgumentError('rezerwacja bez miejsc');
    }
    const message = `Rezerwacja: ${reservation.title}, ${reservation.start.toString()}`
      + `, miejsc: ${reservation.seats}. Zaplac w ciagu 15 minut.`;
    const mime = `To: ${reservation.email}\r\nSubject: Rezerwacja\r\n\r\n${message}`;
    const reply = this.mail.send(reservation.email, mime);
    if (!reply.startsWith('250')) {
      throw new IllegalStateError(`SMTP odrzucil: ${reply}`);
    }
    return `potwierdzono: ${reservation.email}`;
  }
}
