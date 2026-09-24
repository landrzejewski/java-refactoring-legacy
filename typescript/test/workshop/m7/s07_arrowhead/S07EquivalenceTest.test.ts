import { describe } from 'vitest';

import { BookingAttempt } from '../../../../src/workshop/m7/s07_arrowhead/BookingAttempt.js';
import * as start from '../../../../src/workshop/m7/s07_arrowhead/start/BookingGate.js';
import * as step1 from '../../../../src/workshop/m7/s07_arrowhead/step1/BookingGate.js';
import * as step2 from '../../../../src/workshop/m7/s07_arrowhead/step2/BookingGate.js';
import * as step3 from '../../../../src/workshop/m7/s07_arrowhead/step3/BookingGate.js';
import { Scene } from '../../support/scene.js';

/**
 * Macierz gałęzi: każda ścieżka plus kombinacje sprawdzające priorytet warunków.
 * Obserwujemy wynik ORAZ audyt - guard clause wstawiona w book() zgubiłaby wpis.
 */
interface Gate {
  book(attempt: BookingAttempt): string;
  audit(): readonly string[];
}

function attempt(found: boolean, open: boolean, blocked: boolean, requested: number, free: number): BookingAttempt {
  return new BookingAttempt('anna@kino.pl', found, open, blocked, requested, free);
}

function observe(gates: () => Gate): (attempt: BookingAttempt) => string {
  return (a) => {
    const gate = gates();
    const result = gate.book(a);
    return `${result} [${gate.audit().join(', ')}]`;
  };
}

describe('S07EquivalenceTest', () => {
  describe('everyStepKeepsBranchesPriorityAndAudit', () => {
    Scene.variants<BookingAttempt, string>()
      .variant('start', observe(() => new start.BookingGate()))
      .variant('step1', observe(() => new step1.BookingGate()))
      .variant('step2', observe(() => new step2.BookingGate()))
      .variant('step3', observe(() => new step3.BookingGate()))
      .expect('sciezka glowna', attempt(true, true, false, 2, 5), 'BOOKED [anna@kino.pl -> BOOKED]')
      .expect('dokladnie tyle wolnych', attempt(true, true, false, 5, 5), 'BOOKED [anna@kino.pl -> BOOKED]')
      .expect('brak seansu wygrywa ze wszystkim', attempt(false, false, true, 0, 0),
        'NO_SCREENING [anna@kino.pl -> NO_SCREENING]')
      .expect('sprzedaz zamknieta przed blokada klienta', attempt(true, false, true, 2, 5),
        'SALES_CLOSED [anna@kino.pl -> SALES_CLOSED]')
      .expect('blokada klienta przed liczba miejsc', attempt(true, true, true, 0, 5),
        'CUSTOMER_BLOCKED [anna@kino.pl -> CUSTOMER_BLOCKED]')
      .expect('zero miejsc przed brakiem wolnych', attempt(true, true, false, 0, 0),
        'NO_SEATS_REQUESTED [anna@kino.pl -> NO_SEATS_REQUESTED]')
      .expect('wyprzedane', attempt(true, true, false, 6, 5), 'SOLD_OUT [anna@kino.pl -> SOLD_OUT]')
      .tests();
  });
});
