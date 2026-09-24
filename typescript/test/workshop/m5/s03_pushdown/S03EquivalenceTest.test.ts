import { describe } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import * as start from '../../../../src/workshop/m5/s03_pushdown/start/BoxOffice.js';
import * as step1 from '../../../../src/workshop/m5/s03_pushdown/step1/BoxOffice.js';
import * as step2 from '../../../../src/workshop/m5/s03_pushdown/step2/BoxOffice.js';
import * as step3 from '../../../../src/workshop/m5/s03_pushdown/step3/BoxOffice.js';
import { Scene } from '../../support/scene.js';

interface Sale {
  readonly kind: string;
  readonly basePrice: string;
  readonly vip: boolean;
}

/** Test równoważności: sprzedaż (z dopłatą VIP i bez) daje tę samą cenę w start i każdym kroku. */
describe('S03EquivalenceTest', () => {
  describe('everyStepSellsForTheSamePrice', () => {
    Scene.variants<Sale, string>()
      .variant('start', (s) => new start.BoxOffice().sell(s.kind, Money.of(s.basePrice), s.vip).toString())
      .variant('step1', (s) => new step1.BoxOffice().sell(s.kind, Money.of(s.basePrice), s.vip).toString())
      .variant('step2', (s) => new step2.BoxOffice().sell(s.kind, Money.of(s.basePrice), s.vip).toString())
      .variant('step3', (s) => new step3.BoxOffice().sell(s.kind, Money.of(s.basePrice), s.vip).toString())
      .expect('normalny 2D', { kind: 'NORMAL', basePrice: '25.00', vip: false }, '25.00')
      .expect('normalny 2D z dopłatą VIP', { kind: 'NORMAL', basePrice: '25.00', vip: true }, '35.00')
      .expect('studencki 3D', { kind: 'STUDENT', basePrice: '32.00', vip: false }, '24.00')
      .expect('studencki 3D - prośba o VIP ignorowana', { kind: 'STUDENT', basePrice: '32.00', vip: true }, '24.00')
      .tests();
  });
});
