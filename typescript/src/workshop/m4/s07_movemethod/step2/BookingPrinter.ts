import type { Booking } from './Booking.js';

/**
 * Krok 2 (rozwiązanie): Move Method `remainingSeats`
 * -> `Screening.freeSeatsWithout(seat)`. BookingPrinter zostaje koordynatorem wydruku:
 * składa tekst z tego, co wiedzą Booking i Screening.
 */
export class BookingPrinter {
  print(booking: Booking): string {
    return 'Rezerwacja ' + booking.id + '\n'
      + booking.screening.headline() + '\n'
      + 'Miejsce: ' + booking.seat + '\n'
      + 'Pozostale wolne: [' + booking.screening.freeSeatsWithout(booking.seat).join(', ') + ']\n';
  }
}
