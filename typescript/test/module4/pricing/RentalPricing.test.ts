import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../../src/shared/errors.js';
import { EquipmentType } from '../../../src/module4/model/EquipmentType.js';
import { RentalRequest } from '../../../src/module4/model/RentalRequest.js';
import { PriceBreakdown } from '../../../src/module4/pricing/PriceBreakdown.js';
import { RentalPricing } from '../../../src/module4/pricing/RentalPricing.js';

function completeRates(): Map<EquipmentType, Decimal> {
  return new Map([
    [EquipmentType.DRILL, new Decimal('39.99')],
    [EquipmentType.GENERATOR, new Decimal('120.00')],
  ]);
}

describe('RentalPricingTest', () => {
  it('calculatesApprovedPriceBreakdown', () => {
    const request = new RentalRequest('Acme', EquipmentType.GENERATOR, 8, true, true);

    const price = RentalPricing.standard().calculate(request);

    const expected = new PriceBreakdown(
      new Decimal('960.00'),
      new Decimal('96.00'),
      new Decimal('64.00'),
      new Decimal('25.00'),
      new Decimal('953.00'),
      new Decimal('219.19'),
      new Decimal('1172.19'),
    );
    expect(price).toEqual(expected);
    expect(price.equals(expected)).toBe(true);
  });

  it('rejectsIncompleteRateConfiguration', () => {
    const incompleteRates = new Map([[EquipmentType.DRILL, new Decimal('39.99')]]);

    expect(() => new RentalPricing(incompleteRates, new Decimal('0.10')))
      .toThrow(IllegalArgumentError);
  });

  it('rejectsNonPositiveDailyRate', () => {
    const rates = completeRates();
    rates.set(EquipmentType.DRILL, new Decimal(0));

    expect(() => new RentalPricing(rates, new Decimal('0.10'))).toThrow(IllegalArgumentError);
  });

  it('rejectsNegativeDailyRate', () => {
    const rates = completeRates();
    rates.set(EquipmentType.DRILL, new Decimal('-1.00'));

    expect(() => new RentalPricing(rates, new Decimal('0.10'))).toThrow(IllegalArgumentError);
  });

  it('rejectsDailyRateThatRoundsToZero', () => {
    const rates = completeRates();
    rates.set(EquipmentType.DRILL, new Decimal('0.004'));

    expect(() => new RentalPricing(rates, new Decimal('0.10'))).toThrow(IllegalArgumentError);
  });

  it('rejectsDiscountOutsideClosedUnitInterval', () => {
    const rates = completeRates();

    expect.soft(() => new RentalPricing(rates, new Decimal('-0.01'))).toThrow(IllegalArgumentError);
    expect.soft(() => new RentalPricing(rates, new Decimal('1.01'))).toThrow(IllegalArgumentError);
  });

  it('ownsDefensiveCopyOfDailyRates', () => {
    const rates = completeRates();
    const pricing = new RentalPricing(rates, new Decimal('0.10'));

    rates.set(EquipmentType.DRILL, new Decimal('1.00'));
    const price = pricing.calculate(new RentalRequest('Acme', EquipmentType.DRILL, 1, false, false));

    expect(price.baseRentalCost.toFixed(2)).toBe('39.99');
  });
});
