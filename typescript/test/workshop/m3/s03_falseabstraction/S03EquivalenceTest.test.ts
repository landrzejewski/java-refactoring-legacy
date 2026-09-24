import { describe } from 'vitest';

import * as startPass from '../../../../src/workshop/m3/s03_falseabstraction/start/PassCounter.js';
import * as startTicket from '../../../../src/workshop/m3/s03_falseabstraction/start/TicketCounter.js';
import * as step1Pass from '../../../../src/workshop/m3/s03_falseabstraction/step1/PassCounter.js';
import * as step1Ticket from '../../../../src/workshop/m3/s03_falseabstraction/step1/TicketCounter.js';
import * as step2Pass from '../../../../src/workshop/m3/s03_falseabstraction/step2/PassCounter.js';
import * as step2Ticket from '../../../../src/workshop/m3/s03_falseabstraction/step2/TicketCounter.js';
import { Scene } from '../../support/scene.js';

interface TicketCase {
  readonly format: string;
  readonly morning: boolean;
  readonly ownGlasses: boolean;
}

const ticketCase = (format: string, morning: boolean, ownGlasses: boolean): TicketCase => ({ format, morning, ownGlasses });

/** Rozbicie fałszywej abstrakcji nie zmienia cen biletów ani karnetów. */
describe('S03EquivalenceTest', () => {
  describe('ticketsCostTheSame', () => {
    Scene.variants<TicketCase, string>()
      .variant('start', (c) => new startTicket.TicketCounter().ticket(c.format, c.morning, c.ownGlasses).toFixed(2))
      .variant('step1', (c) => new step1Ticket.TicketCounter().ticket(c.format, c.morning, c.ownGlasses).toFixed(2))
      .variant('step2', (c) => new step2Ticket.TicketCounter().ticket(c.format, c.morning, c.ownGlasses).toFixed(2))
      .expect('3D rano, bez wlasnych okularow', ticketCase('3D', true, false), '30.00')
      .expect('3D wieczorem, wlasne okulary', ticketCase('3D', false, true), '32.00')
      .expect('IMAX wieczorem', ticketCase('IMAX', false, false), '40.00')
      .expect('2D rano', ticketCase('2D', true, false), '20.00')
      .tests();
  });

  describe('passesCostTheSame', () => {
    Scene.variants<number, string>()
      .variant('start', (n) => new startPass.PassCounter().pass(n).toFixed(2))
      .variant('step1', (n) => new step1Pass.PassCounter().pass(n).toFixed(2))
      .variant('step2', (n) => new step2Pass.PassCounter().pass(n).toFixed(2))
      .expect('karnet na 10 wejsc', 10, '200.00')
      .expect('karnet na 5 wejsc', 5, '100.00')
      .tests();
  });
});
