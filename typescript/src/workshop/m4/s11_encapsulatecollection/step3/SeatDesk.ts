import type { Seat } from '../Seat.js';
import type { Booking } from './Booking.js';

/** Od kroku 1: klient przepisany na operacje właściciela - nie dotyka już listy. */
export class SeatDesk {
  select(booking: Booking, seat: Seat): void {
    booking.addSeat(seat);
  }

  release(booking: Booking, seat: Seat): void {
    booking.removeSeat(seat);
  }
}
