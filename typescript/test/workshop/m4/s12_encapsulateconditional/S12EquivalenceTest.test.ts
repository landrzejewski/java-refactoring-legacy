import { describe } from 'vitest';

import { Booking } from '../../../../src/workshop/m4/s12_encapsulateconditional/Booking.js';
import * as start from '../../../../src/workshop/m4/s12_encapsulateconditional/start/RefundCalculator.js';
import * as step1 from '../../../../src/workshop/m4/s12_encapsulateconditional/step1/RefundCalculator.js';
import * as step2 from '../../../../src/workshop/m4/s12_encapsulateconditional/step2/RefundCalculator.js';
import * as step3 from '../../../../src/workshop/m4/s12_encapsulateconditional/step3/RefundCalculator.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { LocalDateTime } from '../../../../src/workshop/shared/time.js';
import { Scene } from '../../support/scene.js';

const START = LocalDateTime.of(2026, 9, 25, 20, 0);
const SEVENTY = Money.of('70.00');

interface Refund {
  readonly booking: Booking;
  readonly now: LocalDateTime;
}

const refund = (booking: Booking, now: LocalDateTime): Refund => ({ booking, now });

/** Test równoważności: ta sama kwota zwrotu - oba boki progu 24 h, start seansu, promocje, null. */
describe('S12EquivalenceTest', () => {
  describe('everyStepRefundsTheSameAmount', () => {
    Scene.variants<Refund, string>()
      .variant('start', (r) => new start.RefundCalculator().refund(r.booking, r.now).toString())
      .variant('step1', (r) => new step1.RefundCalculator().refund(r.booking, r.now).toString())
      .variant('step2', (r) => new step2.RefundCalculator().refund(r.booking, r.now).toString())
      .variant('step3', (r) => new step3.RefundCalculator().refund(r.booking, r.now).toString())
      .expect('dokładnie 24 h przed - 100% minus 3.00',
        refund(new Booking('PAID', START, SEVENTY, null), START.minusHours(24)), '67.00')
      .expect('24 h minus minuta - 50% minus 3.00',
        refund(new Booking('PAID', START, SEVENTY, null), START.minusHours(24).plusMinutes(1)),
        '32.00')
      .expect('w chwili startu - brak zwrotu',
        refund(new Booking('PAID', START, SEVENTY, null), START), '0.00')
      .expect('nieopłacona - brak zwrotu',
        refund(new Booking('NEW', START, SEVENTY, null), START.plusDays(-3)), '0.00')
      .expect('darmowy bilet z promocji - brak zwrotu',
        refund(new Booking('PAID', START, SEVENTY, 'FREE-100'), START.plusDays(-3)), '0.00')
      .expect('inna promocja - zwrot jak zwykle',
        refund(new Booking('PAID', START, SEVENTY, 'STUDENT'), START.plusDays(-3)), '67.00')
      .expect('tani bilet: 50% z 5.00 minus 3.00 nie schodzi poniżej zera',
        refund(new Booking('PAID', START, Money.of('5.00'), null), START.minusHours(2)), '0.00')
      .tests();
  });
});
