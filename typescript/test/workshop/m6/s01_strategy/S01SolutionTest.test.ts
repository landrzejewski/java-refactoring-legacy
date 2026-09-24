import { describe, expect, it } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import * as step2 from '../../../../src/workshop/m6/s01_strategy/step2/TicketPricer.js';
import { DiscountPrograms } from '../../../../src/workshop/m6/s01_strategy/step3/DiscountPrograms.js';
import { TicketPricer } from '../../../../src/workshop/m6/s01_strategy/step3/TicketPricer.js';
import { Money } from '../../../../src/workshop/shared/Money.js';

/** Kontrakt rozwiązania i pułapka "momentu wyboru" strategii. */
describe('S01SolutionTest', () => {
  it('strategiesAreSharedBecauseTheyAreStateless', () => {
    expect(DiscountPrograms.forName('STANDARD')).toBe(DiscountPrograms.forName('STANDARD'));
  });

  it('newProgramIsJustAnotherStrategyWithoutTouchingTheContext', () => {
    const blackFriday = new TicketPricer({ discount: (base) => base.percent(50) });
    expect(blackFriday.price(Money.of('40.00'), 'N').equals(Money.of('20.00'))).toBe(true);
  });

  it('beforeStep3BaseIsValidatedBeforeTheProgram', () => {
    const pricer = new step2.TicketPricer();
    const act = () => pricer.price(Money.of('-1.00'), 'N', 'BLACK_FRIDAY');
    expect(act).toThrow(IllegalArgumentError);
    expect(act).toThrow('base price must not be negative');
  });

  it('choosingInConstructorMovesTheUnknownProgramErrorEarlier', () => {
    const act = () => new TicketPricer(DiscountPrograms.forName('BLACK_FRIDAY')).price(Money.of('-1.00'), 'N');
    expect(act).toThrow(IllegalArgumentError);
    expect(act).toThrow('unknown program: BLACK_FRIDAY');
  });
});
