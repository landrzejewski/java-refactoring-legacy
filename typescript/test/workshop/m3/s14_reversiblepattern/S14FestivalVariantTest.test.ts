import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';

import { IllegalArgumentError } from '../../../../src/shared/errors.js';
import { Deal } from '../../../../src/workshop/m3/s14_reversiblepattern/Deal.js';
import * as step1 from '../../../../src/workshop/m3/s14_reversiblepattern/step1/DistributorSettlement.js';
import * as step2 from '../../../../src/workshop/m3/s14_reversiblepattern/step2/DistributorSettlement.js';
import * as step3 from '../../../../src/workshop/m3/s14_reversiblepattern/step3/DistributorSettlement.js';

/**
 * Wariant festiwalowy (zmiana zachowania, więc bez start - ten jest edytowany na żywo):
 * w kroku 1 obsługuje go adapter, od kroku 2 umowy festiwalowe są odrzucane.
 */
describe('S14FestivalVariantTest', () => {
  const FESTIVAL = new Deal('Amator', 'FESTIVAL');
  const REVENUE = new Decimal('3000.00');

  it('step1AdapterPaysTheFestivalFeeConvertedFromCents', () => {
    const settlement = new step1.DistributorSettlement();
    expect(settlement.payout(FESTIVAL, 1, REVENUE).toFixed(2)).toBe('300.00');
    expect(settlement.payout(FESTIVAL, 3, REVENUE).toFixed(2)).toBe('150.00');
  });

  it('afterTheVariantIsGoneFestivalDealsAreRejected', () => {
    expect(() => new step2.DistributorSettlement().payout(FESTIVAL, 1, REVENUE)).toThrow(IllegalArgumentError);
    expect(() => new step3.DistributorSettlement().payout(FESTIVAL, 1, REVENUE)).toThrow(IllegalArgumentError);
  });
});
