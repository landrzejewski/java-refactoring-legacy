import type { Booking } from './Booking.js';
import type { Screening } from './Screening.js';

/**
 * Krok 1: Move Method `screeningLine` -> `Screening.headline()`.
 * Metoda używała tylko danych seansu, więc właścicielem jest Screening.
 * Wywołanie po przeniesieniu czyta się `booking.screening.headline()`.
 */
export class BookingPrinter {
  print(booking: Booking): string {
    return 'Rezerwacja ' + booking.id + '\n'
      + booking.screening.headline() + '\n'
      + 'Miejsce: ' + booking.seat + '\n'
      + 'Pozostale wolne: [' + this.remainingSeats(booking.screening, booking.seat).join(', ') + ']\n';
  }

  private remainingSeats(s: Screening, seat: number): number[] {
    const free = [...s.freeSeats];
    free.splice(free.indexOf(seat), 1);
    return free;
  }
}
