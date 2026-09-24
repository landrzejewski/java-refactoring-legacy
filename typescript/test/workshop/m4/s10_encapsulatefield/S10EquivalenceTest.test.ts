import { describe, expect, it } from 'vitest';

import { IllegalStateError } from '../../../../src/shared/errors.js';
import * as startOffice from '../../../../src/workshop/m4/s10_encapsulatefield/start/BoxOffice.js';
import * as start from '../../../../src/workshop/m4/s10_encapsulatefield/start/Reservation.js';
import * as step1Office from '../../../../src/workshop/m4/s10_encapsulatefield/step1/BoxOffice.js';
import * as step1 from '../../../../src/workshop/m4/s10_encapsulatefield/step1/Reservation.js';
import * as step2Office from '../../../../src/workshop/m4/s10_encapsulatefield/step2/BoxOffice.js';
import * as step2 from '../../../../src/workshop/m4/s10_encapsulatefield/step2/Reservation.js';
import * as step3Office from '../../../../src/workshop/m4/s10_encapsulatefield/step3/BoxOffice.js';
import * as step3 from '../../../../src/workshop/m4/s10_encapsulatefield/step3/Reservation.js';
import { Scene } from '../../support/scene.js';

const PAY_AND_ENTER = ['pay', 'checkIn'];
const CANCEL = ['cancel'];
const GUEST = ['guest'];
const EXPIRED_THEN_PAY = ['expire', 'pay'];
const PAY_TWICE = ['pay', 'pay'];
const ENTER_UNPAID = ['checkIn'];
const GUEST_ON_CANCELLED = ['cancel', 'guest'];

/** Wspólne API kas ze wszystkich etapów - każda przyjmuje własny typ rezerwacji. */
interface Office<R> {
  pay(r: R): void;
  checkIn(r: R): void;
  cancel(r: R): void;
  expire(r: R): void;
  guestEntry(r: R): void;
}

/** Wykonuje polecenia i zapisuje status po każdym (albo BLAD, gdy operacja rzuciła wyjątek). */
function trace<R extends { readonly status: string }>(commands: readonly string[], office: Office<R>,
  reservation: R): string {
  const result: string[] = [];
  for (const command of commands) {
    try {
      switch (command) {
        case 'pay': office.pay(reservation); break;
        case 'checkIn': office.checkIn(reservation); break;
        case 'cancel': office.cancel(reservation); break;
        case 'expire': office.expire(reservation); break;
        default: office.guestEntry(reservation);
      }
      result.push(reservation.status);
    } catch (error) {
      if (!(error instanceof IllegalStateError)) {
        throw error;
      }
      result.push('BLAD');
    }
  }
  return result.join(',');
}

/** Ścieżki dozwolone - wspólne oczekiwania dla wszystkich wariantów, także step3. */
function withAllowedPaths(scene: Scene<readonly string[], string>): Scene<readonly string[], string> {
  return scene
    .expect('zapłata i wejście', PAY_AND_ENTER, 'PAID,USED')
    .expect('anulowanie nowej', CANCEL, 'CANCELLED')
    .expect('gość na nową rezerwację', GUEST, 'USED');
}

/**
 * start, step1, step2 - refaktoryzacja: identyczny ślad statusów, także dla niedozwolonych przejść.
 * step3 - świadoma zmiana zachowania: dozwolone ścieżki bez zmian, niedozwolone kończą się BLAD.
 */
describe('S10EquivalenceTest', () => {
  describe('encapsulationKeepsBehaviour', () => {
    withAllowedPaths(Scene.variants<readonly string[], string>()
      .variant('start', (commands) => trace(commands, new startOffice.BoxOffice(), new start.Reservation()))
      .variant('step1', (commands) => trace(commands, new step1Office.BoxOffice(), new step1.Reservation()))
      .variant('step2', (commands) => trace(commands, new step2Office.BoxOffice(), new step2.Reservation())))
      .expect('ZASTANE: płatność po wygaśnięciu cicho ignorowana', EXPIRED_THEN_PAY, 'EXPIRED,EXPIRED')
      .expect('ZASTANE: druga płatność cicho ignorowana', PAY_TWICE, 'PAID,PAID')
      .expect('ZASTANE: wejście bez płatności cicho ignorowane', ENTER_UNPAID, 'NEW')
      .expect('ZASTANE: gość wchodzi na anulowaną rezerwację', GUEST_ON_CANCELLED, 'CANCELLED,USED')
      .tests();
  });

  describe('step3RejectsIllegalTransitions', () => {
    withAllowedPaths(Scene.variants<readonly string[], string>()
      .variant('step3', (commands) => trace(commands, new step3Office.BoxOffice(), new step3.Reservation())))
      .expect('ZMIANA: płatność po wygaśnięciu odrzucona', EXPIRED_THEN_PAY, 'EXPIRED,BLAD')
      .expect('ZMIANA: druga płatność odrzucona', PAY_TWICE, 'PAID,BLAD')
      .expect('ZMIANA: wejście bez płatności odrzucone', ENTER_UNPAID, 'BLAD')
      .expect('ZMIANA: gość nie wejdzie na anulowaną rezerwację', GUEST_ON_CANCELLED, 'CANCELLED,BLAD')
      .tests();
  });

  it('startFieldIsPublicFromStep1ItIsNot', () => {
    // start: status to własne pole instancji; od step1 - akcesor na prototypie, a dane w polu #status.
    expect(Object.hasOwn(new start.Reservation(), 'status')).toBe(true);
    expect(Object.hasOwn(new step1.Reservation(), 'status')).toBe(false);
    // Od step2 nie ma settera: zapis jest błędem kompilacji, a w czasie wykonania - TypeError.
    const reservation = new step2.Reservation();
    // @ts-expect-error TS2540: status jest tylko do odczytu (sam getter).
    expect(() => { reservation.status = 'USED'; }).toThrow(TypeError);
  });
});
