import { Decimal } from 'decimal.js';

import { Reservation } from './Reservation.js';

/** Krok 2: import bez zmian, a mimo to chroniony przez konstruktor. */
export class ReservationImport {
  importLine(line: string): string {
    const [email = '', seats = '', total = ''] = line.split(';');
    const reservation =
      new Reservation(email, Number.parseInt(seats, 10), new Decimal(total));
    return `zaimportowano: ${reservation.email}, miejsc ${reservation.seats}`
      + `, kwota ${reservation.total.toFixed(2)}`;
  }
}
