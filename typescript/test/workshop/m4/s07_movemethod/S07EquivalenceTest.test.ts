import { describe } from 'vitest';

import { BookingData } from '../../../../src/workshop/m4/s07_movemethod/BookingData.js';
import * as startBooking from '../../../../src/workshop/m4/s07_movemethod/start/Booking.js';
import * as start from '../../../../src/workshop/m4/s07_movemethod/start/BookingPrinter.js';
import * as startScreening from '../../../../src/workshop/m4/s07_movemethod/start/Screening.js';
import * as step1Booking from '../../../../src/workshop/m4/s07_movemethod/step1/Booking.js';
import * as step1 from '../../../../src/workshop/m4/s07_movemethod/step1/BookingPrinter.js';
import * as step1Screening from '../../../../src/workshop/m4/s07_movemethod/step1/Screening.js';
import * as step2Booking from '../../../../src/workshop/m4/s07_movemethod/step2/Booking.js';
import * as step2 from '../../../../src/workshop/m4/s07_movemethod/step2/BookingPrinter.js';
import * as step2Screening from '../../../../src/workshop/m4/s07_movemethod/step2/Screening.js';
import { LocalDateTime } from '../../../../src/workshop/shared/time.js';
import { Scene } from '../../support/scene.js';

const DIUNA = new BookingData('R-1', 'Diuna', 3, LocalDateTime.of(2026, 9, 25, 20, 0),
  1, [1, 2, 3, 4, 5], 1);

/** Test równoważności: ten sam wydruk rezerwacji po każdym przeniesieniu metody. */
describe('S07EquivalenceTest', () => {
  describe('everyStepPrintsTheSameBooking', () => {
    Scene.variants<BookingData, string>()
      .variant('start', (d) => new start.BookingPrinter().print(
        new startBooking.Booking(d.id,
          new startScreening.Screening(d.title, d.format, d.start, d.hall, d.freeSeats), d.seat)))
      .variant('step1', (d) => new step1.BookingPrinter().print(
        new step1Booking.Booking(d.id,
          new step1Screening.Screening(d.title, d.format, d.start, d.hall, d.freeSeats), d.seat)))
      .variant('step2', (d) => new step2.BookingPrinter().print(
        new step2Booking.Booking(d.id,
          new step2Screening.Screening(d.title, d.format, d.start, d.hall, d.freeSeats), d.seat)))
      .expect('IMAX, miejsce 1 (wartość 1 != indeks 1)', DIUNA, `Rezerwacja R-1
Diuna (IMAX), sala 1, 2026-09-25 20:00
Miejsce: 1
Pozostale wolne: [2, 3, 4, 5]
`)
      .expect('3D rano, miejsce 8 (indeks 8 nie istnieje)',
        new BookingData('R-2', 'Kraina Lodu', 2, LocalDateTime.of(2026, 9, 26, 10, 30),
          2, [7, 8, 9], 8), `Rezerwacja R-2
Kraina Lodu (3D), sala 2, 2026-09-26 10:30
Miejsce: 8
Pozostale wolne: [7, 9]
`)
      .expect('2D, miejsce 2 na pozycji 2 - ten przypadek NIE odróżni usuwania po indeksie',
        new BookingData('R-3', 'Amator', 1, LocalDateTime.of(2026, 9, 26, 18, 0),
          3, [3, 1, 2], 2), `Rezerwacja R-3
Amator (2D), sala 3, 2026-09-26 18:00
Miejsce: 2
Pozostale wolne: [3, 1]
`)
      .tests();
  });
});
