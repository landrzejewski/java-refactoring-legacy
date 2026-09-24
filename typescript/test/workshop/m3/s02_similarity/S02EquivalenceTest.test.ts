import { Decimal } from 'decimal.js';
import { describe } from 'vitest';

import * as start1 from '../../../../src/workshop/m3/s02_similarity/start/OnlineCheckout.js';
import * as start2 from '../../../../src/workshop/m3/s02_similarity/start/RefundDesk.js';
import * as step1a from '../../../../src/workshop/m3/s02_similarity/step1/OnlineCheckout.js';
import * as step1b from '../../../../src/workshop/m3/s02_similarity/step1/RefundDesk.js';
import * as step2a from '../../../../src/workshop/m3/s02_similarity/step2/OnlineCheckout.js';
import * as step2b from '../../../../src/workshop/m3/s02_similarity/step2/RefundDesk.js';
import * as step3a from '../../../../src/workshop/m3/s02_similarity/step3/OnlineCheckout.js';
import * as step3b from '../../../../src/workshop/m3/s02_similarity/step3/RefundDesk.js';
import { Scene } from '../../support/scene.js';

interface Case {
  readonly ticketPrices: readonly Decimal[];
  readonly paid: Decimal;
  readonly refundPercent: number;
}

function of(paid: string, percent: number, ...prices: string[]): Case {
  return { ticketPrices: prices.map((price) => new Decimal(price)), paid: new Decimal(paid), refundPercent: percent };
}

interface Checkout { total(ticketPrices: readonly Decimal[]): Decimal }
interface Desk { refund(paidForTickets: Decimal, percent: number): Decimal }

function run(checkout: Checkout, desk: Desk, c: Case): string {
  return `online ${checkout.total(c.ticketPrices).toFixed(2)} / zwrot ${desk.refund(c.paid, c.refundPercent).toFixed(2)}`;
}

/** Rozdzielenie fałszywie scalonych opłat nie zmienia żadnej kwoty. */
describe('S02EquivalenceTest', () => {
  describe('everyStepChargesAndRefundsTheSame', () => {
    Scene.variants<Case, string>()
      .variant('start', (c) => run(new start1.OnlineCheckout(), new start2.RefundDesk(), c))
      .variant('step1', (c) => run(new step1a.OnlineCheckout(), new step1b.RefundDesk(), c))
      .variant('step2', (c) => run(new step2a.OnlineCheckout(), new step2b.RefundDesk(), c))
      .variant('step3', (c) => run(new step3a.OnlineCheckout(), new step3b.RefundDesk(), c))
      .expect('dwa bilety, pelny zwrot', of('65.00', 100, '40.00', '25.00'),
        'online 69.00 / zwrot 62.00')
      .expect('jeden bilet, zwrot 50%', of('19.00', 50, '19.00'),
        'online 21.00 / zwrot 6.50')
      .expect('zwrot po starcie nie schodzi ponizej zera', of('17.50', 0, '17.50'),
        'online 19.50 / zwrot 0.00')
      .expect('potracenie wieksze niz polowa ceny', of('5.00', 50, '25.00', '25.00', '25.00'),
        'online 81.00 / zwrot 0.00')
      .tests();
  });
});
