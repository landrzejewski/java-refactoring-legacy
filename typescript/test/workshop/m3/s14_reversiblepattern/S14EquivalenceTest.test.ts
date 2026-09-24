import { Decimal } from 'decimal.js';
import { describe } from 'vitest';

import { Deal } from '../../../../src/workshop/m3/s14_reversiblepattern/Deal.js';
import * as start from '../../../../src/workshop/m3/s14_reversiblepattern/start/DistributorSettlement.js';
import * as step1 from '../../../../src/workshop/m3/s14_reversiblepattern/step1/DistributorSettlement.js';
import * as step2 from '../../../../src/workshop/m3/s14_reversiblepattern/step2/DistributorSettlement.js';
import * as step3 from '../../../../src/workshop/m3/s14_reversiblepattern/step3/DistributorSettlement.js';
import { Scene } from '../../support/scene.js';

interface Case {
  readonly week: number;
  readonly revenue: string;
}

const DUNE = new Deal('Diuna', 'PERCENT');

type Settlement = { payout(deal: Deal, week: number, ticketRevenue: Decimal): Decimal };

function settle(settlement: Settlement, c: Case): string {
  return settlement.payout(DUNE, c.week, new Decimal(c.revenue)).toFixed(2);
}

/** Model procentowy rozlicza się tak samo przed wzorcem, ze wzorcem i po jego usunięciu. */
describe('S14EquivalenceTest', () => {
  describe('percentageDealsSettleTheSame', () => {
    Scene.variants<Case, string>()
      .variant('start', (c) => settle(new start.DistributorSettlement(), c))
      .variant('step1', (c) => settle(new step1.DistributorSettlement(), c))
      .variant('step2', (c) => settle(new step2.DistributorSettlement(), c))
      .variant('step3', (c) => settle(new step3.DistributorSettlement(), c))
      .expect('tydzien 1: 50%', { week: 1, revenue: '2000.00' }, '1000.00')
      .expect('tydzien 2: 40%', { week: 2, revenue: '2000.00' }, '800.00')
      .expect('tydzien 3: 35% ponizej gwarancji', { week: 3, revenue: '1000.00' }, '500.00')
      .expect('tydzien 5: 35%', { week: 5, revenue: '4000.00' }, '1400.00')
      .tests();
  });
});
