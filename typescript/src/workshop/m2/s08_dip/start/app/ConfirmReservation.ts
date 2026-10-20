import { IllegalArgumentError, IllegalStateError } from '../../../../../shared/errors.js';
import type { Reservation } from '../../Reservation.js';
import { SmtpMailSender } from '../infra/SmtpMailSender.js';

/**
 * Start: polityka (potwierdzenie rezerwacji) sama tworzy szczegół techniczny,
 * składa nagłówki MIME i interpretuje kody SMTP. Zależność źródłowa i przepływ
 * sterowania biegną w tę samą stronę: app -> infra. Nie da się jej przetestować
 * bez "wysłania maila" i nie da się zmienić kanału (SMS) bez edycji polityki.
 */
export class ConfirmReservation {
  private readonly mail = new SmtpMailSender('smtp.kino.pl', 25);

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
