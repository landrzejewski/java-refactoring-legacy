import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Booking } from '../Booking.js';
import { BookingStatus } from '../BookingStatus.js';
import { CardTerminal } from './CardTerminal.js';
import { CinemaMailer } from './CinemaMailer.js';
import type { Mailer } from './Mailer.js';

/**
 * Krok 1: Parameterize Constructor z Mailer - maile stają się obserwowalne.
 * Kod poza tym bez zmian, więc regresja nadal tu jest - ale teraz test ją WIDZI i dokumentuje.
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
    const charged = CardTerminal.charge(cardNumber, booking.amount);
    if (charged) {
      booking.markPaid();
    } else {
      this.mailer(booking.email, `Platnosc odrzucona ${booking.id}`);
    }
    this.mailer(booking.email, `Bilety ${booking.id} oplacone: ${booking.amount.toString()}`);
    return charged ? 'OK' : 'DECLINED';
  }
}
