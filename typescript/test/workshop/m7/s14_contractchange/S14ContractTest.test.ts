import { describe, expect, it } from 'vitest';

import * as start from '../../../../src/workshop/m7/s14_contractchange/start/RefundCalculator.js';
import * as step1 from '../../../../src/workshop/m7/s14_contractchange/step1/RefundCalculator.js';
import * as step2 from '../../../../src/workshop/m7/s14_contractchange/step2/RefundCalculator.js';
import * as step3 from '../../../../src/workshop/m7/s14_contractchange/step3/RefundCalculator.js';
import { Scene } from '../../support/scene.js';

/**
 * Część przypadków nie zmienia się nigdy. Dwa przypadki brzegowe pokazują, który krok
 * zmienił kontrakt: krok 2 zmienił format i zaokrąglenie, krok 3 świadomie tylko zaokrąglenie.
 */
class Cancel {
  constructor(readonly ticketsPaid: number, readonly minutesBeforeStart: number) {}
}

const HALF_CENT = new Cancel(64.35, 120);
const AFTER_START = new Cancel(114.00, -10);

const runStart = (c: Cancel): string => new start.RefundCalculator().refund(c.ticketsPaid, c.minutesBeforeStart);
const runStep1 = (c: Cancel): string => new step1.RefundCalculator().refund(c.ticketsPaid, c.minutesBeforeStart);
const runStep2 = (c: Cancel): string => new step2.RefundCalculator().refund(c.ticketsPaid, c.minutesBeforeStart);
const runStep3 = (c: Cancel): string => new step3.RefundCalculator().refund(c.ticketsPaid, c.minutesBeforeStart);

describe('S14ContractTest', () => {
  describe('ordinaryRefundsNeverChange', () => {
    Scene.variants<Cancel, string>()
      .variant('start', runStart)
      .variant('step1', runStep1)
      .variant('step2', runStep2)
      .variant('step3', runStep3)
      .expect('>= 24h: 100% - 3.00', new Cancel(114.00, 48 * 60), '111.00')
      .expect('dokladnie 24h', new Cancel(114.00, 24 * 60), '111.00')
      .expect('< 24h: 50% - 3.00', new Cancel(104.40, 10 * 60), '49.20')
      .expect('< 24h, kwota z groszami', new Cancel(153.00, 30), '73.50')
      .tests();
  });

  it('refactoringStepKeepsHistoricalRoundingAndFormat', () => {
    expect(runStart(HALF_CENT)).toBe('29.17');
    expect(runStep1(HALF_CENT)).toBe('29.17');
    expect(runStart(AFTER_START)).toBe('0.00');
    expect(runStep1(AFTER_START)).toBe('0.00');
  });

  it('byTheWayStepChangedTwoThingsAtOnce', () => {
    expect(runStep2(HALF_CENT), 'zaokraglenie: HALF_UP na dokladnej wartosci 29.175').toBe('29.18');
    expect(runStep2(AFTER_START), "format: '0' zamiast 0.00").toBe('0');
  });

  it('deliberateStepChangesOnlyTheApprovedRounding', () => {
    expect(runStep3(HALF_CENT), 'zatwierdzona zmiana kontraktu').toBe('29.18');
    expect(runStep3(AFTER_START), 'format przywrocony').toBe('0.00');
  });
});
