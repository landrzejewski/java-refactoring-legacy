import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { Booking } from '../Booking.js';
import { BookingStatus } from '../BookingStatus.js';
import { CardTerminal } from './CardTerminal.js';
import { CinemaMailer } from './CinemaMailer.js';
import type { Mailer } from './Mailer.js';
import type { PaymentGateway } from './PaymentGateway.js';

/**
 * Krok 3 (rozwiązanie): drugi seam - PaymentGateway. Test zapisuje maile i obciążenia
 * do JEDNEGO dziennika, więc widzi też ich kolejność. Razem ze stanem rezerwacji
 * i wyjątkami to pełny wektor obserwowalnego zachowania tej metody.
 */
export class TicketCheckout {
  private readonly mailer: Mailer;
  private readonly gateway: PaymentGateway;

  constructor(
    mailer: Mailer = (to, text) => CinemaMailer.send(to, text),
    gateway: PaymentGateway = (card, amount) => CardTerminal.charge(card, amount),
  ) {
    this.mailer = requireNonNull(mailer, 'mailer');
    this.gateway = requireNonNull(gateway, 'gateway');
  }

  pay(booking: Booking, card: string | null): string {
    const cardNumber = requireNonNull(card, 'card');
    if (booking.status !== BookingStatus.NEW) {
      return `ERROR: status ${booking.status}`;
    }
    if (this.gateway(cardNumber, booking.amount)) {
      booking.markPaid();
      this.mailer(booking.email, `Bilety ${booking.id} oplacone: ${booking.amount.toString()}`);
      return 'OK';
    }
    this.mailer(booking.email, `Platnosc odrzucona ${booking.id}`);
    return 'DECLINED';
  }
}
