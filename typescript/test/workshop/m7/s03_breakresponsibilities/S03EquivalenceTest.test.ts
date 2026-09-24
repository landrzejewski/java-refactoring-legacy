import { describe } from 'vitest';

import { BookingRequest } from '../../../../src/workshop/m7/s03_breakresponsibilities/BookingRequest.js';
import { Outbox } from '../../../../src/workshop/m7/s03_breakresponsibilities/Outbox.js';
import * as start from '../../../../src/workshop/m7/s03_breakresponsibilities/start/BookingDesk.js';
import * as step1 from '../../../../src/workshop/m7/s03_breakresponsibilities/step1/BookingDesk.js';
import * as step2 from '../../../../src/workshop/m7/s03_breakresponsibilities/step2/BookingDesk.js';
import * as step3 from '../../../../src/workshop/m7/s03_breakresponsibilities/step3/BookingDesk.js';
import { Scene } from '../../support/scene.js';

/** Test równoważności: ten sam wynik i ta sama skrzynka nadawcza w start i każdym kroku. */
function observe(desk: (outbox: Outbox, request: BookingRequest) => string): (request: BookingRequest) => string {
  return (request) => {
    const outbox = new Outbox();
    const result = desk(outbox, request);
    return `${result} | [${outbox.sent().join(', ')}]`;
  };
}

describe('S03EquivalenceTest', () => {
  describe('everyStepBooksAndNotifiesTheSame', () => {
    Scene.variants<BookingRequest, string>()
      .variant('start', observe((outbox, request) => new start.BookingDesk(outbox).book(request)))
      .variant('step1', observe((outbox, request) => new step1.BookingDesk(outbox).book(request)))
      .variant('step2', observe((outbox, request) => new step2.BookingDesk(outbox).book(request)))
      .variant('step3', observe((outbox, request) => new step3.BookingDesk(outbox).book(request)))
      .expect('IMAX, jedno miejsce VIP',
        new BookingRequest('anna@kino.pl', 'IMAX', ['A5', 'C10']),
        'OK 90.00 | [anna@kino.pl: Rezerwacja 2 miejsc (VIP: 1), do zaplaty 90.00]')
      .expect('2D bez VIP',
        new BookingRequest('jan@kino.pl', '2D', ['B3']),
        'OK 25.00 | [jan@kino.pl: Rezerwacja 1 miejsc, do zaplaty 25.00]')
      .expect('niepoprawny e-mail ma pierwszenstwo',
        new BookingRequest('jan-kino.pl', '2D', []),
        'ERROR: niepoprawny e-mail | []')
      .expect('brak miejsc',
        new BookingRequest('jan@kino.pl', '3D', []),
        'ERROR: brak miejsc | []')
      .expect('niepoprawne miejsce',
        new BookingRequest('jan@kino.pl', '3D', ['A1', 'Z99']),
        'ERROR: niepoprawne miejsce Z99 | []')
      .tests();
  });
});
