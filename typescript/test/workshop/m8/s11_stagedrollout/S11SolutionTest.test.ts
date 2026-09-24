import { describe, expect, it } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import * as start from '../../../../src/workshop/m8/s11_stagedrollout/start/CheckoutRouter.js';
import { RolloutPolicy } from '../../../../src/workshop/m8/s11_stagedrollout/step3/RolloutPolicy.js';

const CUSTOMERS = Array.from({ length: 1000 }, (_, i) => 'klient' + i + '@kino.pl');

/** Test polityki wdrożenia etapowego z kroku 3 (procent, wyjątki, wyłącznik). */
describe('S11SolutionTest', () => {
  it('sameCustomerAlwaysGetsTheSamePath', () => {
    const policy = new RolloutPolicy(30, [], false);
    for (const email of CUSTOMERS) {
      expect(new RolloutPolicy(30, [], false).allows(email), email).toBe(policy.allows(email));
    }
    expect(RolloutPolicy.bucket('  Ola@Kino.PL ')).toBe(RolloutPolicy.bucket('ola@kino.pl'));
  });

  it('percentOfCustomersIsRoughlyRespected', () => {
    const policy = new RolloutPolicy(20, [], false);
    const included = CUSTOMERS.filter((email) => policy.allows(email)).length;
    expect(included > 150 && included < 250, '20% z 1000 klientów, było: ' + included).toBe(true);
    expect(CUSTOMERS.filter((email) => new RolloutPolicy(0, [], false).allows(email))).toHaveLength(0);
    expect(CUSTOMERS.filter((email) => new RolloutPolicy(100, [], false).allows(email))).toHaveLength(1000);
  });

  it('increasingPercentNeverRemovesAnyone', () => {
    const ten = new RolloutPolicy(10, [], false);
    const twenty = new RolloutPolicy(20, [], false);
    expect(CUSTOMERS.filter((email) => ten.allows(email)).every((email) => twenty.allows(email))).toBe(true);
  });

  it('allowListWorksAtZeroPercent', () => {
    const policy = new RolloutPolicy(0, ['anna@kino.pl'], false);
    expect(policy.allows('Anna@Kino.pl')).toBe(true);
    expect(policy.allows('ola@kino.pl')).toBe(false);
  });

  it('killSwitchOverridesPercentAndAllowList', () => {
    const killed = new RolloutPolicy(100, ['anna@kino.pl'], true);
    expect(killed.allows('anna@kino.pl')).toBe(false);
    expect(CUSTOMERS.some((email) => killed.allows(email))).toBe(false);
  });

  it('invalidPercentIsRejected', () => {
    expect(() => new RolloutPolicy(101, [], false)).toThrow(IllegalArgumentError);
    expect(() => new RolloutPolicy(-1, [], false)).toThrow(IllegalArgumentError);
  });

  it('startComparesEmailLiterally', () => {
    const router = new start.CheckoutRouter();
    expect(router.useNewCheckout('Anna@kino.pl'), 'stary if porównuje e-mail dosłownie').toBe(false);
  });
});
