import { describe } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import * as startCorporate from '../../../../src/workshop/m5/s14_reuse/start/CorporateAccount.js';
import * as startLoyalty from '../../../../src/workshop/m5/s14_reuse/start/LoyaltyAccount.js';
import * as startReport from '../../../../src/workshop/m5/s14_reuse/start/LoyaltyReport.js';
import * as step1Corporate from '../../../../src/workshop/m5/s14_reuse/step1/CorporateAccount.js';
import * as step1Loyalty from '../../../../src/workshop/m5/s14_reuse/step1/LoyaltyAccount.js';
import * as step1Report from '../../../../src/workshop/m5/s14_reuse/step1/LoyaltyReport.js';
import * as step2Corporate from '../../../../src/workshop/m5/s14_reuse/step2/CorporateAccount.js';
import * as step2Loyalty from '../../../../src/workshop/m5/s14_reuse/step2/LoyaltyAccount.js';
import * as step2Report from '../../../../src/workshop/m5/s14_reuse/step2/LoyaltyReport.js';
import { Scene } from '../../support/scene.js';

interface History {
  readonly corporate: boolean;
  readonly owner: string;
  readonly payments: readonly string[];
}

/** Test równoważności: naliczanie punktów i raport identyczne dla obu kont w start i każdym kroku. */
describe('S14EquivalenceTest', () => {
  describe('everyStepReportsPointsTheSameWay', () => {
    Scene.variants<History, string>()
      .variant('start', (h) => {
        const account = h.corporate ? new startCorporate.CorporateAccount(h.owner) : new startLoyalty.LoyaltyAccount(h.owner);
        h.payments.forEach((p) => account.earn(Money.of(p)));
        return new startReport.LoyaltyReport().line(account);
      })
      .variant('step1', (h) => {
        const account = h.corporate ? new step1Corporate.CorporateAccount(h.owner) : new step1Loyalty.LoyaltyAccount(h.owner);
        h.payments.forEach((p) => account.earn(Money.of(p)));
        return new step1Report.LoyaltyReport().line(account);
      })
      .variant('step2', (h) => {
        const account = h.corporate ? new step2Corporate.CorporateAccount(h.owner) : new step2Loyalty.LoyaltyAccount(h.owner);
        h.payments.forEach((p) => account.earn(Money.of(p)));
        return new step2Report.LoyaltyReport().line(account);
      })
      .expect('klient: pełne dziesiątki', { corporate: false, owner: 'anna@kino.pl', payments: ['45.00', '32.00'] },
        'anna@kino.pl: 7 pkt')
      .expect('firma: duża transakcja', { corporate: true, owner: 'Kino-Tech', payments: ['999.99'] },
        'Kino-Tech: 99 pkt')
      .expect('klient bez zakupów', { corporate: false, owner: 'jan@kino.pl', payments: [] }, 'jan@kino.pl: 0 pkt')
      .tests();
  });
});
