import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Booking } from '../Booking.js';
import { BookingStatus } from '../BookingStatus.js';
import { CardTerminal } from './CardTerminal.js';
import { CinemaMailer } from './CinemaMailer.js';

/**
 * Start: kod po "porządkach" kolegi (Consolidate Duplicate Conditional Fragments) -
 * mail potwierdzający wysunięto za if, więc dostaje go także klient z odrzuconą kartą.
 * Test sprawdza tylko wynik (OK / DECLINED) i jest zielony. Regresja przeszła.
 */
export class TicketCheckout {
  pay(booking: Booking, card: string | null): string {
    const cardNumber = requireNonNull(card, 'card');
    if (booking.status !== BookingStatus.NEW) {
      return `ERROR: status ${booking.status}`;
    }
    const charged = CardTerminal.charge(cardNumber, booking.amount);
    if (charged) {
      booking.markPaid();
    } else {
      CinemaMailer.send(booking.email, `Platnosc odrzucona ${booking.id}`);
    }
    CinemaMailer.send(booking.email, `Bilety ${booking.id} oplacone: ${booking.amount.toString()}`);
    return charged ? 'OK' : 'DECLINED';
  }
}
