import { describe } from 'vitest';

import { Seat } from '../../../../src/workshop/m4/s11_encapsulatecollection/Seat.js';
import * as start from '../../../../src/workshop/m4/s11_encapsulatecollection/start/Booking.js';
import * as startDesk from '../../../../src/workshop/m4/s11_encapsulatecollection/start/SeatDesk.js';
import * as step1 from '../../../../src/workshop/m4/s11_encapsulatecollection/step1/Booking.js';
import * as step1Desk from '../../../../src/workshop/m4/s11_encapsulatecollection/step1/SeatDesk.js';
import * as step2 from '../../../../src/workshop/m4/s11_encapsulatecollection/step2/Booking.js';
import * as step2Desk from '../../../../src/workshop/m4/s11_encapsulatecollection/step2/SeatDesk.js';
import * as step3 from '../../../../src/workshop/m4/s11_encapsulatecollection/step3/Booking.js';
import * as step3Desk from '../../../../src/workshop/m4/s11_encapsulatecollection/step3/SeatDesk.js';
import type { Money } from '../../../../src/workshop/shared/Money.js';
import { Scene } from '../../support/scene.js';

/** Polecenie kasy: +rząd/numer wybiera miejsce, -rząd/numer je zwalnia. */
const COMMANDS = ['+1/5', '+10/3', '-1/5', '+12/1', '+10/3'];

interface Desk<B> {
  select(booking: B, seat: Seat): void;
  release(booking: B, seat: Seat): void;
}

function run<B extends { readonly seats: readonly Seat[]; total(): Money }>(commands: readonly string[],
  desk: Desk<B>, booking: B): string {
  for (const command of commands) {
    const [row = '', number = ''] = command.substring(1).split('/');
    const seat = new Seat(Number.parseInt(row, 10), Number.parseInt(number, 10));
    if (command.startsWith('+')) {
      desk.select(booking, seat);
    } else {
      desk.release(booking, seat);
    }
  }
  return '[' + booking.seats.join(', ') + '] -> ' + booking.total().toString();
}

/** Test równoważności: przez SeatDesk wszystkie warianty dają te same miejsca i tę samą kwotę. */
describe('S11EquivalenceTest', () => {
  describe('everyVariantSelectsSeatsTheSameWay', () => {
    Scene.variants<readonly string[], string>()
      .variant('start', (commands) => run(commands, new startDesk.SeatDesk(), new start.Booking()))
      .variant('step1', (commands) => run(commands, new step1Desk.SeatDesk(), new step1.Booking()))
      .variant('step2', (commands) => run(commands, new step2Desk.SeatDesk(), new step2.Booking()))
      .variant('step3', (commands) => run(commands, new step3Desk.SeatDesk(), new step3.Booking()))
      .expect('wybór, zwolnienie, duplikat zostaje (lista, nie zbiór)', COMMANDS,
        '[10/3, 12/1, 10/3] -> 105.00')
      .expect('pusta rezerwacja', [], '[] -> 0.00')
      .expect('zwolnienie miejsca, którego nie ma', ['+1/1', '-2/2'], '[1/1] -> 25.00')
      .tests();
  });
});
