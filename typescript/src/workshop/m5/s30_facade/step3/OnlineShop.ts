import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Money } from '../../../shared/Money.js';
import type { TicketOrder } from '../TicketOrder.js';
import type { BookingFacade } from './BookingFacade.js';

/** Krok 3: bez zmian - sklep deleguje cały zakup do fasady; zna już tylko swoją opłatę rezerwacyjną. */
export class OnlineShop {
  private static readonly BOOKING_FEE = Money.of('2.00');

  private readonly booking: BookingFacade;

  constructor(booking: BookingFacade) {
    this.booking = requireNonNull(booking, 'booking');
  }

  buy(order: TicketOrder): string {
    return this.booking.book(order, OnlineShop.BOOKING_FEE);
  }
}
