import { IllegalStateError } from '../../../../shared/errors.js';
import type { PaidBooking } from '../PaidBooking.js';

/** Produkcyjna baza. Konstruktor otwiera połączenie - poza serwerownią kina to się nie uda. */
export class LegacyDatabase {
  constructor() {
    throw new IllegalStateError('brak polaczenia z jdbc:oracle:thin:@prod-db:1521/CINEMA');
  }

  paidBookings(): readonly PaidBooking[] {
    return [];
  }

  markReminded(_bookingId: string): void {
    // UPDATE bookings SET reminded = 1 WHERE id = ?
  }
}
