import { Decimal } from 'decimal.js';

import { Reservation } from './Reservation.js';

/** Start: import z pliku partnera "email;miejsca;kwota" - tworzy model z pominięciem walidacji. */
export class ReservationImport {
  importLine(line: string): string {
    const [email = '', seats = '', total = ''] = line.split(';');
    const reservation = new Reservation();
    reservation.email = email;
    reservation.seats = Number.parseInt(seats, 10);
    reservation.total = new Decimal(total);
    return `zaimportowano: ${reservation.email}, miejsc ${reservation.seats}`
      + `, kwota ${reservation.total.toFixed(2)}`;
  }
}
