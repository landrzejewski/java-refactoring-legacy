import type { Reservation } from './Reservation.js';

/** Kasa - klient pola status. guestEntry łamie niezmiennik z zewnątrz. */
export class BoxOffice {
  pay(r: Reservation): void {
    if (r.status === 'NEW') {
      r.status = 'PAID';
    }
  }

  checkIn(r: Reservation): void {
    if (r.status === 'PAID') {
      r.status = 'USED';
    }
  }

  cancel(r: Reservation): void {
    if (r.status === 'NEW' || r.status === 'PAID') {
      r.status = 'CANCELLED';
    }
  }

  expire(r: Reservation): void {
    if (r.status === 'NEW') {
      r.status = 'EXPIRED';
    }
  }

  /** Wejście gościa kierownika - "na skróty", bez płatności i bez sprawdzania statusu. */
  guestEntry(r: Reservation): void {
    r.status = 'USED';
  }
}
