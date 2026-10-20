import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Booking } from '../Booking.js';
import { BookingStatus } from '../BookingStatus.js';
import { CardTerminal } from './CardTerminal.js';
import { CinemaMailer } from './CinemaMailer.js';
import type { Mailer } from './Mailer.js';

/**
 * Krok 2: naprawa regresji pod ochroną testu z kroku 1 - mail potwierdzający wraca
 * do gałęzi sukcesu. Wynik metody się nie zmienia, zmienia się efekt uboczny.
 */
export class TicketCheckout {
  private readonly mailer: Mailer;

  constructor(mailer: Mailer = (to, text) => CinemaMailer.send(to, text)) {
    this.mailer = requireNonNull(mailer, 'mailer');
  }

  pay(booking: Booking, card: string | null): string {
    const cardNumber = requireNonNull(card, 'card');
    if (booking.status !== BookingStatus.NEW) {
      return `ERROR: status ${booking.status}`;
    }
    if (CardTerminal.charge(cardNumber, booking.amount)) {
      booking.markPaid();
      this.mailer(booking.email, `Bilety ${booking.id} oplacone: ${booking.amount.toString()}`);
      return 'OK';
    }
    this.mailer(booking.email, `Platnosc odrzucona ${booking.id}`);
    return 'DECLINED';
  }
}
