import { describe } from 'vitest';

import * as startCustomer from '../../../../src/workshop/m7/s09_doublenegative/start/Customer.js';
import * as start from '../../../../src/workshop/m7/s09_doublenegative/start/LoungeAccess.js';
import * as startVoucher from '../../../../src/workshop/m7/s09_doublenegative/start/Voucher.js';
import * as step1Customer from '../../../../src/workshop/m7/s09_doublenegative/step1/Customer.js';
import * as step1 from '../../../../src/workshop/m7/s09_doublenegative/step1/LoungeAccess.js';
import * as step1Voucher from '../../../../src/workshop/m7/s09_doublenegative/step1/Voucher.js';
import * as step2Customer from '../../../../src/workshop/m7/s09_doublenegative/step2/Customer.js';
import * as step2 from '../../../../src/workshop/m7/s09_doublenegative/step2/LoungeAccess.js';
import * as step2Voucher from '../../../../src/workshop/m7/s09_doublenegative/step2/Voucher.js';
import * as step3Customer from '../../../../src/workshop/m7/s09_doublenegative/step3/Customer.js';
import * as step3 from '../../../../src/workshop/m7/s09_doublenegative/step3/LoungeAccess.js';
import * as step3Voucher from '../../../../src/workshop/m7/s09_doublenegative/step3/Voucher.js';
import { LocalDate } from '../../../../src/workshop/shared/time.js';
import { Scene } from '../../support/scene.js';

/**
 * Test równoważności. Wejście opisujemy "po staremu" (notVip), a adapter kroku 3
 * musi odwrócić wartość przy budowie obiektu - to jest dokładnie migracja granicy.
 */
class Visit {
  constructor(readonly notVip: boolean, readonly voucherValidUntil: LocalDate | null) {}
}

const TODAY = LocalDate.of(2026, 3, 10);

describe('S09EquivalenceTest', () => {
  describe('everyStepGrantsTheSameAccess', () => {
    Scene.variants<Visit, string>()
      .variant('start', (v) => {
        const customer = new startCustomer.Customer('anna@kino.pl', v.notVip);
        const voucher = v.voucherValidUntil === null ? null
          : new startVoucher.Voucher('LOUNGE', v.voucherValidUntil);
        const lounge = new start.LoungeAccess();
        return `${lounge.canEnter(customer, voucher, TODAY)} ${lounge.badge(customer)}`;
      })
      .variant('step1', (v) => {
        const customer = new step1Customer.Customer('anna@kino.pl', v.notVip);
        const voucher = v.voucherValidUntil === null ? null
          : new step1Voucher.Voucher('LOUNGE', v.voucherValidUntil);
        const lounge = new step1.LoungeAccess();
        return `${lounge.canEnter(customer, voucher, TODAY)} ${lounge.badge(customer)}`;
      })
      .variant('step2', (v) => {
        const customer = new step2Customer.Customer('anna@kino.pl', v.notVip);
        const voucher = v.voucherValidUntil === null ? null
          : new step2Voucher.Voucher('LOUNGE', v.voucherValidUntil);
        const lounge = new step2.LoungeAccess();
        return `${lounge.canEnter(customer, voucher, TODAY)} ${lounge.badge(customer)}`;
      })
      .variant('step3', (v) => {
        const customer = new step3Customer.Customer('anna@kino.pl', !v.notVip);
        const voucher = v.voucherValidUntil === null ? null
          : new step3Voucher.Voucher('LOUNGE', v.voucherValidUntil);
        const lounge = new step3.LoungeAccess();
        return `${lounge.canEnter(customer, voucher, TODAY)} ${lounge.badge(customer)}`;
      })
      .expect('VIP bez vouchera', new Visit(false, null), 'true VIP')
      .expect('VIP z przeterminowanym voucherem', new Visit(false, TODAY.plusDays(-1)), 'true VIP')
      .expect('zwykly klient bez vouchera', new Visit(true, null), 'false STANDARD')
      .expect('voucher wazny do dzis wlacznie', new Visit(true, TODAY), 'true STANDARD')
      .expect('voucher wygasl wczoraj', new Visit(true, TODAY.plusDays(-1)), 'false STANDARD')
      .tests();
  });
});
