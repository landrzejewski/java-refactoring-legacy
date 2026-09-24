import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m5/s11_constructorcall/start/Ticket.js';
import * as step1 from '../../../../src/workshop/m5/s11_constructorcall/step1/Ticket.js';
import * as step2 from '../../../../src/workshop/m5/s11_constructorcall/step2/Ticket.js';
import { Scene } from '../../support/scene.js';

/** Wspólna część wariantów: zwykły bilet (bez podklasy) ma tę samą etykietę w każdym kroku. */
describe('S11EquivalenceTest', () => {
  describe('everyStepLabelsPlainTicketTheSameWay', () => {
    Scene.variants<string, string>()
      .variant('start', (seat) => new start.Ticket(seat).label())
      .variant('step1', (seat) => new step1.Ticket(seat).label())
      .variant('step2', (seat) => new step2.Ticket(seat).label())
      .expect('zwykłe miejsce', 'H7', 'Miejsce H7')
      .tests();
  });
});
