import { describe } from 'vitest';

import { Order } from '../../../../src/workshop/m4/s04_extractmethod/Order.js';
import * as start from '../../../../src/workshop/m4/s04_extractmethod/start/TicketSummary.js';
import * as step1 from '../../../../src/workshop/m4/s04_extractmethod/step1/TicketSummary.js';
import * as step2 from '../../../../src/workshop/m4/s04_extractmethod/step2/TicketSummary.js';
import * as step3 from '../../../../src/workshop/m4/s04_extractmethod/step3/TicketSummary.js';
import { LocalTime } from '../../../../src/workshop/shared/time.js';
import { Scene } from '../../support/scene.js';

/** Test równoważności: start i każdy krok dają identyczny dokument. */
describe('S04EquivalenceTest', () => {
  describe('everyStepDescribesTicketsTheSameWay', () => {
    Scene.variants<Order, string>()
      .variant('start', (o) => new start.TicketSummary().describe(o))
      .variant('step1', (o) => new step1.TicketSummary().describe(o))
      .variant('step2', (o) => new step2.TicketSummary().describe(o))
      .variant('step3', (o) => new step3.TicketSummary().describe(o))
      .expect('IMAX wieczorem, jedno miejsce VIP',
        new Order('Diuna', 'IMAX', LocalTime.of(20, 0), [5, 10]),
        `BILETY: Diuna
Format: IMAX, start 20:00
Miejsc: 2 (w tym VIP: 1)
Razem: 90.00
`)
      .expect('3D rano bez VIP',
        new Order('Kraina Lodu', '3D', LocalTime.of(11, 0), [1, 2, 3]),
        `BILETY: Kraina Lodu
Format: 3D, start 11:00
Miejsc: 3
Razem: 81.00
`)
      .expect('2D, same miejsca VIP',
        new Order('Amator', '2D', LocalTime.of(18, 30), [10, 11]),
        `BILETY: Amator
Format: 2D, start 18:30
Miejsc: 2 (w tym VIP: 2)
Razem: 70.00
`)
      .expect('puste zamówienie',
        new Order('Amator', '2D', LocalTime.of(18, 30), []),
        `BILETY: Amator
Format: 2D, start 18:30
Miejsc: 0
Razem: 0.00
`)
      .tests();
  });
});
