import { describe } from 'vitest';

import * as start from '../../../../src/workshop/m7/s15_behaviourvector/start/TicketCheckout.js';
import * as step1 from '../../../../src/workshop/m7/s15_behaviourvector/step1/TicketCheckout.js';
import * as step2 from '../../../../src/workshop/m7/s15_behaviourvector/step2/TicketCheckout.js';
import * as step3 from '../../../../src/workshop/m7/s15_behaviourvector/step3/TicketCheckout.js';
import { Scene } from '../../support/scene.js';
import { Payment } from './Payment.js';

/**
 * Słaby test z punktu startu: obserwuje tylko wynik. Jest zielony dla KAŻDEGO wariantu -
 * także dla start i step1, które wysyłają "Bilety oplacone" po odrzuceniu karty.
 */
describe('S15ResultOnlyTest', () => {
  describe('resultOnlyCannotSeeTheRegression', () => {
    Scene.variants<Payment, string>()
      .variant('start', (p) => new start.TicketCheckout().pay(p.booking(), p.card))
      .variant('step1', (p) => new step1.TicketCheckout().pay(p.booking(), p.card))
      .variant('step2', (p) => new step2.TicketCheckout().pay(p.booking(), p.card))
      .variant('step3', (p) => new step3.TicketCheckout().pay(p.booking(), p.card))
      .expect('sukces', Payment.SUCCESS, 'OK')
      .expect('karta odrzucona', Payment.DECLINED, 'DECLINED')
      .expect('juz oplacona', Payment.ALREADY_PAID, 'ERROR: status PAID')
      .tests();
  });
});
