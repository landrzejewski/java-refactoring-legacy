import { Formats } from './Formats.js';
import { LegacyMailer } from './LegacyMailer.js';

/**
 * Krok 2: jedyne miejsce, które zna treść powiadomień i statyczny LegacyMailer.
 * Kolejny krok kampanii mógłby tu wprowadzić seam (interfejs mailera) - bez dotykania CinemaManager.
 * Eksportowany tylko na potrzeby CinemaManager (w Javie klasa pakietowa).
 */
export class NotificationService {
  bookingCreated(email: string, bookingId: string, title: string, seats: string[], total: number): void {
    LegacyMailer.send(email, 'Rezerwacja ' + bookingId,
      'Film: ' + title + ', miejsca: ' + seats.join(',')
        + ', do zaplaty: ' + Formats.amount(total));
  }

  paymentDeclined(email: string, bookingId: string): void {
    LegacyMailer.send(email, 'Platnosc odrzucona', 'Rezerwacja ' + bookingId);
  }

  ticketsPaid(email: string, phone: string | null, bookingId: string, paid: number, points: number): void {
    LegacyMailer.send(email, 'Bilety ' + bookingId, 'Oplacono ' + Formats.amount(paid)
      + ', punkty: +' + points);
    if (phone != null) {
      LegacyMailer.sms(phone, 'CineLegacy: bilety ' + bookingId + ' oplacone');
    }
  }

  bookingExpired(email: string, bookingId: string): void {
    LegacyMailer.send(email, 'Rezerwacja wygasla', bookingId);
  }

  bookingCancelled(email: string, bookingId: string, refund: number): void {
    LegacyMailer.send(email, 'Anulowano ' + bookingId, 'Zwrot: ' + Formats.amount(refund));
  }
}
