import { Booking } from '../../../../src/workshop/m7/s15_behaviourvector/Booking.js';
import { BookingStatus } from '../../../../src/workshop/m7/s15_behaviourvector/BookingStatus.js';
import { Money } from '../../../../src/workshop/shared/Money.js';

/** Wejście testów sceny: stan rezerwacji przed płatnością i numer karty. */
export class Payment {
  static readonly SUCCESS = new Payment(BookingStatus.NEW, '4111111111111111');
  static readonly DECLINED = new Payment(BookingStatus.NEW, '4111111111110000');
  static readonly ALREADY_PAID = new Payment(BookingStatus.PAID, '4111111111111111');
  static readonly NO_CARD = new Payment(BookingStatus.NEW, null);

  constructor(readonly statusBefore: BookingStatus, readonly card: string | null) {}

  booking(): Booking {
    return new Booking('B1', 'anna@kino.pl', Money.of('114.00'), this.statusBefore);
  }
}
