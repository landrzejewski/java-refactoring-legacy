import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { TicketOrder } from '../TicketOrder.js';
import type { BookingFacade } from './BookingFacade.js';

/**
 * Krok 3: bez zmian - kasa korzysta z tej samej fasady co sklep, bez opłaty rezerwacyjnej.
 * Skopiowana orkiestracja zniknęła razem z jej błędem (brak zwolnienia miejsc po odmowie).
 */
export class BoxOffice {
  private readonly booking: BookingFacade;

  constructor(booking: BookingFacade) {
    this.booking = requireNonNull(booking, 'booking');
  }

  sell(order: TicketOrder): string {
    return this.booking.book(order, Money.ZERO);
  }
}
