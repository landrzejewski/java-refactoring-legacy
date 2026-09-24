import { describe } from 'vitest';

import { IllegalStateError } from '../../../../src/shared/errors.js';
import type { Payments } from '../../../../src/workshop/m6/s08_state/Payments.js';
import type { ReservationActions } from '../../../../src/workshop/m6/s08_state/ReservationActions.js';
import * as start from '../../../../src/workshop/m6/s08_state/start/Reservation.js';
import * as step1 from '../../../../src/workshop/m6/s08_state/step1/Reservation.js';
import * as step2 from '../../../../src/workshop/m6/s08_state/step2/Reservation.js';
import * as step3 from '../../../../src/workshop/m6/s08_state/step3/Reservation.js';
import { Scene } from '../../support/scene.js';

/** Przypadek: jak dojść do stanu (akcje), jaka akcja jest testowana, czy bramka działa. */
interface Case {
  readonly setup: readonly string[];
  readonly action: string;
  readonly gatewayDown: boolean;
}

type Factory = (id: string, payments: Payments) => ReservationActions;

// Tabela przejść zapisana PRZED refaktoryzacją: stan x akcja -> wynik. Każdy wariant
// musi dać ten sam status, te same efekty (w tej samej kolejności) i ten sam wyjątek.
const TABLE = `
NEW       | pay    | PAID [charged]
NEW       | use    | ERROR cannot use in NEW -> NEW []
NEW       | expire | EXPIRED [seats released]
NEW       | cancel | CANCELLED [seats released]
PAID      | pay    | ERROR cannot pay in PAID -> PAID [charged]
PAID      | use    | USED [charged, gate opened]
PAID      | expire | ERROR cannot expire in PAID -> PAID [charged]
PAID      | cancel | CANCELLED [charged, refund, seats released]
USED      | pay    | ERROR cannot pay in USED -> USED [charged, gate opened]
USED      | use    | ERROR cannot use in USED -> USED [charged, gate opened]
USED      | expire | ERROR cannot expire in USED -> USED [charged, gate opened]
USED      | cancel | ERROR cannot cancel in USED -> USED [charged, gate opened]
EXPIRED   | pay    | ERROR cannot pay in EXPIRED -> EXPIRED [seats released]
EXPIRED   | use    | ERROR cannot use in EXPIRED -> EXPIRED [seats released]
EXPIRED   | expire | ERROR cannot expire in EXPIRED -> EXPIRED [seats released]
EXPIRED   | cancel | ERROR cannot cancel in EXPIRED -> EXPIRED [seats released]
CANCELLED | pay    | ERROR cannot pay in CANCELLED -> CANCELLED [seats released]
CANCELLED | use    | ERROR cannot use in CANCELLED -> CANCELLED [seats released]
CANCELLED | expire | ERROR cannot expire in CANCELLED -> CANCELLED [seats released]
CANCELLED | cancel | ERROR cannot cancel in CANCELLED -> CANCELLED [seats released]
`;

function setupFor(status: string): string[] {
  switch (status) {
    case 'NEW': return [];
    case 'PAID': return ['pay'];
    case 'USED': return ['pay', 'use'];
    case 'EXPIRED': return ['expire'];
    case 'CANCELLED': return ['cancel'];
    default: throw new Error(status);
  }
}

function play(c: Case, factory: Factory): string {
  const reservation = factory('R1', {
    charge: () => {
      if (c.gatewayDown) {
        throw new IllegalStateError('bramka niedostepna');
      }
    },
  });
  c.setup.forEach((action) => apply(reservation, action));
  const show = () => `${reservation.status()} [${reservation.effects().join(', ')}]`;
  try {
    apply(reservation, c.action);
    return show();
  } catch (error) {
    return `ERROR ${(error as Error).message} -> ${show()}`;
  }
}

function apply(reservation: ReservationActions, action: string): void {
  switch (action) {
    case 'pay': reservation.pay(); break;
    case 'use': reservation.use(); break;
    case 'expire': reservation.expire(); break;
    case 'cancel': reservation.cancel(); break;
    default: throw new Error(action);
  }
}

describe('S08EquivalenceTest', () => {
  describe('everyStepFollowsTheTransitionTable', () => {
    const scene = Scene.variants<Case, string>()
      .variant('start', (c) => play(c, (id, payments) => new start.Reservation(id, payments)))
      .variant('step1', (c) => play(c, (id, payments) => new step1.Reservation(id, payments)))
      .variant('step2', (c) => play(c, (id, payments) => new step2.Reservation(id, payments)))
      .variant('step3', (c) => play(c, (id, payments) => new step3.Reservation(id, payments)));
    for (const line of TABLE.trim().split('\n')) {
      const [from = '', action = '', expected = ''] = line.split('|').map((cell) => cell.trim());
      scene.expect(`${from} + ${action}`, { setup: setupFor(from), action, gatewayDown: false }, expected);
    }
    scene
      .expect('bramka niedostępna: stan i efekty bez zmian',
        { setup: [], action: 'pay', gatewayDown: true }, 'ERROR bramka niedostepna -> NEW []')
      .tests();
  });
});
