import { describe, expect, it } from 'vitest';

import { UnsupportedOperationError } from '../../../../src/shared/errors.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import { CorporateAccount as Step1CorporateAccount } from '../../../../src/workshop/m5/s14_reuse/step1/CorporateAccount.js';
import { LoyaltyAccount as Step1LoyaltyAccount } from '../../../../src/workshop/m5/s14_reuse/step1/LoyaltyAccount.js';
import { CorporateAccount } from '../../../../src/workshop/m5/s14_reuse/step2/CorporateAccount.js';
import { LoyaltyAccount } from '../../../../src/workshop/m5/s14_reuse/step2/LoyaltyAccount.js';
import type { PointsHolder } from '../../../../src/workshop/m5/s14_reuse/step2/PointsHolder.js';

/** Dziedziczenie dla reużycia łamie substytucję; kompozycja + rola przywraca uczciwy kontrakt. */
describe('S14SolutionTest', () => {
  it('regularAccountRedeemsFreeTicket', () => {
    const account = new Step1LoyaltyAccount('anna@kino.pl');
    account.earn(Money.of('1000.00'));
    expect(account.redeemFreeTicket()).toBe(true);
    expect(account.points()).toBe(0);
  });

  it('beforeSplitCorporateAccountBreaksBaseContract', () => {
    const account: Step1LoyaltyAccount = new Step1CorporateAccount('Kino-Tech');
    account.earn(Money.of('1000.00'));
    // pułapka: podtyp odrzuca operację, którą obiecuje nadtyp
    expect(() => account.redeemFreeTicket()).toThrow(UnsupportedOperationError);
  });

  it('solutionCorporateAccountIsNotALoyaltyAccount', () => {
    const corporate = new CorporateAccount('Kino-Tech');
    expect(corporate).not.toBeInstanceOf(LoyaltyAccount);
    // PointsHolder to interfejs (znika po kompilacji) - przypisanie sprawdza kompilator.
    const holder: PointsHolder = corporate;
    expect(holder.owner()).toBe('Kino-Tech');
    expect('redeemFreeTicket' in corporate).toBe(false);
  });
});
