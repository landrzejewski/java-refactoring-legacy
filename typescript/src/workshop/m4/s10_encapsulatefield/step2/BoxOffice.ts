import type { Reservation } from './Reservation.js';

/** Krok 2: kasa tylko deleguje - reguły przejść mają jednego właściciela. */
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
