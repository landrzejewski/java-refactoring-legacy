import { describe } from 'vitest';

import { GroupOrder } from '../../../../src/workshop/m7/s02_methodobject/GroupOrder.js';
import { Quote } from '../../../../src/workshop/m7/s02_methodobject/Quote.js';
import * as start from '../../../../src/workshop/m7/s02_methodobject/start/GroupPricing.js';
import * as step1 from '../../../../src/workshop/m7/s02_methodobject/step1/GroupPricing.js';
import * as step2 from '../../../../src/workshop/m7/s02_methodobject/step2/GroupPricing.js';
import * as step3 from '../../../../src/workshop/m7/s02_methodobject/step3/GroupPricing.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { LocalTime } from '../../../../src/workshop/shared/time.js';
import { Scene } from '../../support/scene.js';

/** Test równoważności: start i każdy krok wyceniają zamówienia identycznie. */
const EVENING = LocalTime.of(20, 0);
const MORNING = LocalTime.of(11, 0);

function quote(tickets: string, fees: string, total: string, points: number): string {
  return new Quote(Money.of(tickets), Money.of(fees), Money.of(total), points).toString();
}

describe('S02EquivalenceTest', () => {
  describe('everyStepQuotesTheSame', () => {
    Scene.variants<GroupOrder, string>()
      .variant('start', (o) => new start.GroupPricing().quote(o).toString())
      .variant('step1', (o) => new step1.GroupPricing().quote(o).toString())
      .variant('step2', (o) => new step2.GroupPricing().quote(o).toString())
      .variant('step3', (o) => new step3.GroupPricing().quote(o).toString())
      .expect('IMAX wieczorem N+S+E, jedno VIP, online',
        new GroupOrder('IMAX', EVENING, ['NORMAL', 'STUDENT', 'SENIOR'], 1, false, true),
        quote('108.00', '6.00', '114.00', 10))
      .expect('3D rano, dwoje dzieci, okulary z kina, VIP, kasa',
        new GroupOrder('3D', MORNING, ['NORMAL', 'CHILD', 'CHILD', 'NORMAL'], 1, false, false),
        quote('104.40', '0.00', '104.40', 10))
      .expect('grupa szkolna 2D: 8 dzieci + 2 opiekunow, online',
        new GroupOrder('2D', LocalTime.of(18, 30),
          ['CHILD', 'CHILD', 'CHILD', 'CHILD', 'CHILD', 'CHILD', 'CHILD', 'CHILD',
            'NORMAL', 'NORMAL'], 0, false, true),
        quote('153.00', '20.00', '173.00', 15))
      .expect('grupa 2D: rabat 10% z zaokragleniem (231.25 -> 208.12)',
        new GroupOrder('2D', EVENING,
          ['STUDENT', 'STUDENT', 'STUDENT', 'NORMAL', 'NORMAL', 'NORMAL', 'NORMAL',
            'NORMAL', 'NORMAL', 'NORMAL'], 0, false, false),
        quote('208.12', '0.00', '208.12', 20))
      .expect('3D z wlasnymi okularami',
        new GroupOrder('3D', EVENING, ['NORMAL'], 0, true, false),
        quote('32.00', '0.00', '32.00', 3))
      .expect('puste zamowienie',
        new GroupOrder('2D', EVENING, [], 0, false, true),
        quote('0.00', '0.00', '0.00', 0))
      .tests();
  });
});
