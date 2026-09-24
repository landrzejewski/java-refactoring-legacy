import { describe } from 'vitest';

import { TicketQuery } from '../../../../src/workshop/m8/s06_reviewableseries/TicketQuery.js';
import * as start from '../../../../src/workshop/m8/s06_reviewableseries/start/PriceList.js';
import * as step1 from '../../../../src/workshop/m8/s06_reviewableseries/step1/PriceList.js';
import * as step2 from '../../../../src/workshop/m8/s06_reviewableseries/step2/PriceList.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { Scene } from '../../support/scene.js';
import { MONDAY_EVENING, TUESDAY_EVENING, TUESDAY_MORNING } from './S06Fixtures.js';

/** Test równoważności dla commitów refaktoryzacyjnych (1 i 2). Commit 3 zmienia zachowanie - osobny test. */
describe('S06EquivalenceTest', () => {
  describe('refactoringCommitsDoNotChangePrices', () => {
    Scene.variants<TicketQuery, Money>()
      .variant('start', (q) => new start.PriceList().price(q))
      .variant('step1', (q) => new step1.PriceList().price(q))
      .variant('step2', (q) => new step2.PriceList().price(q))
      .expect('2D normalny, poniedziałek', new TicketQuery('2D', 'NORMAL', MONDAY_EVENING, 5), Money.of('25.00'))
      .expect('2D normalny, wtorek', new TicketQuery('2D', 'NORMAL', TUESDAY_EVENING, 5), Money.of('25.00'))
      .expect('IMAX student VIP', new TicketQuery('IMAX', 'STUDENT', MONDAY_EVENING, 10), Money.of('40.00'))
      .expect('3D dziecko rano', new TicketQuery('3D', 'CHILD', TUESDAY_MORNING, 3), Money.of('14.20'))
      .expect('2D senior', new TicketQuery('2D', 'SENIOR', TUESDAY_EVENING, 1), Money.of('17.50'))
      .tests();
  });
});
