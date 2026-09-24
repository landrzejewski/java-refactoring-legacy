import { describe } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import type { Money } from '../../../../src/workshop/shared/Money.js';
import * as start from '../../../../src/workshop/m6/s17_singleton/start/TicketDesk.js';
import * as step1 from '../../../../src/workshop/m6/s17_singleton/step1/TicketDesk.js';
import * as step2 from '../../../../src/workshop/m6/s17_singleton/step2/TicketDesk.js';
import * as step3 from '../../../../src/workshop/m6/s17_singleton/step3/TicketDesk.js';
import { Scene } from '../../support/scene.js';

interface Desk {
  quote(format: string, online: boolean): Money;
}

function safe(desk: Desk): (input: string) => string {
  return (input) => {
    const [format = '', channel = ''] = input.split(' ');
    try {
      return desk.quote(format, channel === 'online').toString();
    } catch (exception) {
      if (exception instanceof IllegalArgumentError) {
        return `ERROR ${exception.message}`;
      }
      throw exception;
    }
  };
}

/** Wejście "FORMAT kanał": wycena bez zmian niezależnie od liczby instancji cennika. */
describe('S17EquivalenceTest', () => {
  describe('everyStepQuotesTheSame', () => {
    Scene.variants<string, string>()
      .variant('start', safe(new start.TicketDesk()))
      .variant('step1', safe(new step1.TicketDesk()))
      .variant('step2', safe(new step2.TicketDesk()))
      .variant('step3', safe(new step3.TicketDesk()))
      .expect('2D kasa', '2D kasa', '25.00')
      .expect('3D online', '3D online', '34.00')
      .expect('IMAX online', 'IMAX online', '42.00')
      .expect('nieznany format', '4DX kasa', 'ERROR unknown format: 4DX')
      .tests();
  });
});
