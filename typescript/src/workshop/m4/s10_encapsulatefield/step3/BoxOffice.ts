import type { Reservation } from './Reservation.js';

/** Krok 3: kasa bez zmian - nowa reguła mieszka w Reservation. */
export class BoxOffice {
  pay(r: Reservation): void {
    r.pay();
  }

  checkIn(r: Reservation): void {
    r.checkIn();
  }

  cancel(r: Reservation): void {
    r.cancel();
  }

  expire(r: Reservation): void {
    r.expire();
  }

  guestEntry(r: Reservation): void {
    r.admitGuestWithoutPayment();
  }
}
