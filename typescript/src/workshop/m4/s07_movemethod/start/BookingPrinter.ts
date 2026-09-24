import type { Booking } from './Booking.js';
import type { Screening } from './Screening.js';

/**
 * Start: wydruk rezerwacji. screeningLine i remainingSeats używają WYŁĄCZNIE danych Screening
 * (Feature Envy). print koordynuje Booking i Screening - ona zostaje tutaj.
 */
export class BookingPrinter {
  print(booking: Booking): string {
    return 'Rezerwacja ' + booking.id + '\n'
      + this.screeningLine(booking.screening) + '\n'
      + 'Miejsce: ' + booking.seat + '\n'
      + 'Pozostale wolne: [' + this.remainingSeats(booking.screening, booking.seat).join(', ') + ']\n';
  }

  private screeningLine(s: Screening): string {
    let format: string;
    switch (s.format) {
      case 3: format = 'IMAX'; break;
      case 2: format = '3D'; break;
      default: format = '2D';
    }
    return s.title + ' (' + format + '), sala ' + s.hall + ', '
      + s.start.toLocalDate().toString() + ' ' + s.start.toLocalTime().toString();
  }

  private remainingSeats(s: Screening, seat: number): number[] {
    const free = [...s.freeSeats];
    free.splice(free.indexOf(seat), 1);
    return free;
  }
}
