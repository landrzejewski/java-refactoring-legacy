import { describe } from 'vitest';

import { LocalTime } from '../../../../src/workshop/shared/time.js';
import * as start from '../../../../src/workshop/m3/s06_yagni/start/TicketPricer.js';
import * as step1 from '../../../../src/workshop/m3/s06_yagni/step1/TicketPricer.js';
import * as step2 from '../../../../src/workshop/m3/s06_yagni/step2/TicketPricer.js';
import * as step3 from '../../../../src/workshop/m3/s06_yagni/step3/TicketPricer.js';
import { TicketQuote } from '../../../../src/workshop/m3/s06_yagni/TicketQuote.js';
import { Scene } from '../../support/scene.js';

/** Silnik reguł i dwa proste warunki dają te same ceny. */
describe('S06EquivalenceTest', () => {
  describe('everyStepPricesTheSame', () => {
    Scene.variants<TicketQuote, string>()
      .variant('start', (q) => new start.TicketPricer().price(q).toFixed(2))
      .variant('step1', (q) => new step1.TicketPricer().price(q).toFixed(2))
      .variant('step2', (q) => new step2.TicketPricer().price(q).toFixed(2))
      .variant('step3', (q) => new step3.TicketPricer().price(q).toFixed(2))
      .expect('IMAX wieczorem, zwykle miejsce', new TicketQuote('IMAX', LocalTime.of(20, 0), 5, 10), '40.00')
      .expect('3D rano, VIP', new TicketQuote('3D', LocalTime.of(10, 0), 12, 10), '37.00')
      .expect('2D 11:59 to jeszcze poranek, pierwszy rzad VIP',
        new TicketQuote('2D', LocalTime.of(11, 59), 10, 10), '30.00')
      .expect('2D 12:00 to juz nie poranek', new TicketQuote('2D', LocalTime.of(12, 0), 9, 10), '25.00')
      .tests();
  });
});
