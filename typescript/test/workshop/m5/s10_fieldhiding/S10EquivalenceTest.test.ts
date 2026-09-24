import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m5/s10_fieldhiding/start/Ticket.js';
import * as step1 from '../../../../src/workshop/m5/s10_fieldhiding/step1/Ticket.js';
import * as step2 from '../../../../src/workshop/m5/s10_fieldhiding/step2/Ticket.js';
import { Scene } from '../../support/scene.js';

/** Wspólna część wariantów: bilet normalny opisuje się tak samo w każdym kroku. */
describe('S10EquivalenceTest', () => {
  describe('everyStepLabelsNormalTicketTheSameWay', () => {
    Scene.variants<string, string>()
      .variant('start', () => new start.Ticket().label())
      .variant('step1', () => new step1.Ticket().label())
      .variant('step2', () => new step2.Ticket().label())
      .expect('bilet normalny', '', 'BILET: NORMAL')
      .tests();
  });
});
