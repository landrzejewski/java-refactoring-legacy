import { describe, expect, it } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import { DayPolicies } from '../../../../src/workshop/m6/s20_decisionmap/step2/DayPolicies.js';
import type { DayPolicy } from '../../../../src/workshop/m6/s20_decisionmap/step2/DayPolicy.js';
import { ShowPricing } from '../../../../src/workshop/m6/s20_decisionmap/step2/ShowPricing.js';
import { Format } from '../../../../src/workshop/m6/s20_decisionmap/step3/Format.js';

/** Kryterium wyboru: która zmiana jest tania w danej strukturze. */
describe('S20SolutionTest', () => {
  it('pathAMakesANewDayCampaignCheap', () => {
    const seniorWednesday: DayPolicy = (base) => base.minus(Money.of('5.00'));
    const pricing = new ShowPricing((day) => (day === 'WEDNESDAY' ? seniorWednesday : DayPolicies.standard(day)));
    expect(pricing.price('WEDNESDAY', 'IMAX')).toEqual(Money.of('35.00'));
    expect(pricing.price('TUESDAY', 'IMAX')).toEqual(Money.of('28.00'));
  });

  it('pathBKeepsEverythingAboutAFormatInOnePlace', () => {
    const imax = Format.of('IMAX');
    expect(imax.priceOn('SATURDAY')).toEqual(Money.of('42.00'));
  });
});
