import type { Seat } from '../Seat.js';
import type { Booking } from './Booking.js';

/** Klient kolekcji: wybór i zwolnienie miejsca - bezpośrednio na liście właściciela. */
export class SeatDesk {
  select(booking: Booking, seat: Seat): void {
    booking.seats.push(seat);
  }

  release(booking: Booking, seat: Seat): void {
    const index = booking.seats.findIndex((s) => s.equals(seat));
    if (index >= 0) {
      booking.seats.splice(index, 1);
    }
  }
}
