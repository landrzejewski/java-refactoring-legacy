import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';
import { Booking } from '../Booking.js';

/**
 * Krok 3 (bez zmian): stary format wiersza - nadal zapisywany, żeby stara wersja mogła czytać dane.
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
