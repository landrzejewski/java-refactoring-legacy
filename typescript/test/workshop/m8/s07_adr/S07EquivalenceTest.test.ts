import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m8/s07_adr/start/BookingService.js';
import * as step1 from '../../../../src/workshop/m8/s07_adr/step1/BookingService.js';
import * as step2 from '../../../../src/workshop/m8/s07_adr/step2/BookingService.js';
import { Scene } from '../../support/scene.js';

interface Order {
  readonly organizer: string;
  readonly tickets: number;
  readonly format: string;
}

interface Service {
  book(organizer: string, tickets: number, format: string): string;
  sentMails(): readonly string[];
}

function run(service: Service, order: Order): string {
  return service.book(order.organizer, order.tickets, order.format) + ' [' + service.sentMails().join(', ') + ']';
}

/** Test równoważności: dochodzenie do zgodności z ADR nie zmienia odpowiedzi ani wysłanych maili. */
describe('S07EquivalenceTest', () => {
  describe('everyStepBooksTheSameWay', () => {
    Scene.variants<Order, string>()
      .variant('start', (order) => run(new start.BookingService(), order))
      .variant('step1', (order) => run(new step1.BookingService(), order))
      .variant('step2', (order) => run(new step2.BookingService(), order))
      .expect('grupa 12 biletów 2D', { organizer: 'anna@kino.pl', tickets: 12, format: '2D' },
        'DO ZAPLATY 270.00 [anna@kino.pl: rabat grupowy dla 12 biletow]')
      .expect('2 bilety IMAX', { organizer: 'jan@kino.pl', tickets: 2, format: 'IMAX' }, 'DO ZAPLATY 80.00 []')
      .expect('grupa 10 biletów 3D', { organizer: 'ola@kino.pl', tickets: 10, format: '3D' },
        'DO ZAPLATY 288.00 [ola@kino.pl: rabat grupowy dla 10 biletow]')
      .tests();
  });
});
