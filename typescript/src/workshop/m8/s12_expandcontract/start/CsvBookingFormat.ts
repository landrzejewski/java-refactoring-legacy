import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';
import { Booking } from '../Booking.js';

/**
 * Start: stary format wiersza - B1;anna@kino.pl;A5,A10;84.00
 * (bez wersji, średnik w danych psuje wiersz).
 */
export class CsvBookingFormat {
  private constructor() {}

  static write(booking: Booking): string {
    return booking.id + ';' + booking.email + ';' + booking.seats.join(',')
      + ';' + booking.total.toString();
  }

  static read(line: string): Booking {
    const parts = line.split(';');
    return new Booking(parts[0]!, parts[1]!, parts[2]!.split(','),
      new Money(new Decimal(parts[3]!)));
  }
}
