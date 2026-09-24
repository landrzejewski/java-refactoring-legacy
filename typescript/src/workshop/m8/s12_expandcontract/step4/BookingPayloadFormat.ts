import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import { Booking } from '../Booking.js';

/** Krok 4 (bez zmian): wersjonowany format payload - jedyny format rezerwacji. */
export class BookingPayloadFormat {
  private static readonly VERSION = 'v2';

  private constructor() {}

  static write(booking: Booking): string {
    return BookingPayloadFormat.VERSION + '|id=' + booking.id + '|email=' + booking.email
      + '|seats=' + booking.seats.join(' ') + '|total=' + booking.total.toString();
  }

  static read(payload: string): Booking {
    const parts = payload.split('|');
    if (parts[0] !== BookingPayloadFormat.VERSION) {
      throw new IllegalArgumentError('Nieznana wersja formatu: ' + parts[0]);
    }
    const fields = new Map<string, string>();
    for (const part of parts.slice(1)) {
      const at = part.indexOf('=');
      fields.set(part.substring(0, at), part.substring(at + 1));
    }
    return new Booking(fields.get('id')!, fields.get('email')!,
      fields.get('seats')!.split(' '),
      new Money(new Decimal(fields.get('total')!)));
  }
}
