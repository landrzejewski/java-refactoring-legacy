import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import * as startService from '../../../../src/workshop/m3/s15_invariants/start/BookingService.js';
import * as startImport from '../../../../src/workshop/m3/s15_invariants/start/ReservationImport.js';
import * as step1Service from '../../../../src/workshop/m3/s15_invariants/step1/BookingService.js';
import * as step1Import from '../../../../src/workshop/m3/s15_invariants/step1/ReservationImport.js';
import * as step2Service from '../../../../src/workshop/m3/s15_invariants/step2/BookingService.js';
import * as step2Import from '../../../../src/workshop/m3/s15_invariants/step2/ReservationImport.js';
import { Scene } from '../../support/scene.js';

interface Request {
  readonly email: string;
  readonly seats: number;
  readonly total: string;
}

function outcome(action: () => string): string {
  try {
    return action();
  } catch (error) {
    if (error instanceof IllegalArgumentError) {
      return `blad: ${error.message}`;
    }
    throw error;
  }
}

type Service = { book(email: string, seats: number, total: Decimal): string };

function book(service: Service, r: Request): string {
  return outcome(() => service.book(r.email, r.seats, new Decimal(r.total)));
}

/**
 * Ścieżka przez serwis działa identycznie we wszystkich wariantach (także komunikaty błędów).
 * Ścieżka importu: do kroku 1 przepuszcza złe dane (pułapka), w kroku 2 je odrzuca.
 */
describe('S15InvariantsTest', () => {
  describe('bookingServiceBehavesTheSame', () => {
    Scene.variants<Request, string>()
      .variant('start', (r) => book(new startService.BookingService(), r))
      .variant('step1', (r) => book(new step1Service.BookingService(), r))
      .variant('step2', (r) => book(new step2Service.BookingService(), r))
      .expect('poprawna rezerwacja', { email: 'anna@kino.pl', seats: 2, total: '50.00' },
        'zarezerwowano: anna@kino.pl, miejsc 2, kwota 50.00')
      .expect('zly email', { email: 'jan-kino.pl', seats: 1, total: '25.00' }, 'blad: niepoprawny email: jan-kino.pl')
      .expect('zero miejsc', { email: 'jan@kino.pl', seats: 0, total: '0.00' },
        'blad: liczba miejsc musi byc dodatnia: 0')
      .expect('ujemna kwota', { email: 'jan@kino.pl', seats: 1, total: '-5.00' }, 'blad: kwota nie moze byc ujemna: -5.00')
      .tests();
  });

  describe('importOfValidLineBehavesTheSame', () => {
    Scene.variants<string, string>()
      .variant('start', (line) => new startImport.ReservationImport().importLine(line))
      .variant('step1', (line) => new step1Import.ReservationImport().importLine(line))
      .variant('step2', (line) => new step2Import.ReservationImport().importLine(line))
      .expect('poprawny wiersz', 'anna@kino.pl;2;50.00', 'zaimportowano: anna@kino.pl, miejsc 2, kwota 50.00')
      .tests();
  });

  it('importWithoutInvariantsCreatesInvalidReservation', () => {
    // krok 1 = start pod tym względem (start jest edytowany na żywo, więc sprawdzamy kopię)
    expect(new step1Import.ReservationImport().importLine('jan-kino.pl;0;-5.00'))
      .toBe('zaimportowano: jan-kino.pl, miejsc 0, kwota -5.00');
  });

  it('step2ImportCannotCreateInvalidReservation', () => {
    const importer = new step2Import.ReservationImport();
    expect(() => importer.importLine('jan-kino.pl;0;-5.00'))
      .toThrow(new IllegalArgumentError('niepoprawny email: jan-kino.pl'));
    expect(() => importer.importLine('jan-kino.pl;0;-5.00')).toThrow(IllegalArgumentError);
  });
});
