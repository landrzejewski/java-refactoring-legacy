import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import type { DeliveryPricePolicy } from '../../../src/module3/domain/DeliveryPricePolicy.js';
import { ExpressDeliveryPricePolicy } from '../../../src/module3/domain/ExpressDeliveryPricePolicy.js';
import { FuelSurcharge } from '../../../src/module3/domain/FuelSurcharge.js';
import { Parcel } from '../../../src/module3/domain/Parcel.js';
import { ShippingMethod } from '../../../src/module3/domain/ShippingMethod.js';
import { StandardDeliveryPricePolicy } from '../../../src/module3/domain/StandardDeliveryPricePolicy.js';

function policies(): [string, ShippingMethod, DeliveryPricePolicy][] {
  const surcharge = new FuelSurcharge(new Decimal('0.08'));
  return [
    ['standard policy', ShippingMethod.STANDARD, new StandardDeliveryPricePolicy(surcharge)],
    ['express policy', ShippingMethod.EXPRESS, new ExpressDeliveryPricePolicy(surcharge)],
  ];
}

describe('DeliveryPricePolicyContractTest', () => {
  it.each(policies())('everyPolicyObeysTheSubstitutionContract: %s', (_description, expectedMethod, policy) => {
    const parcel = new Parcel(new Decimal('100.00'));

    const firstMethod = policy.method();
    const secondMethod = policy.method();
    const firstResult = policy.priceFor(parcel);
    const secondResult = policy.priceFor(parcel);

    expect.soft(firstMethod).toBe(expectedMethod);
    expect.soft(secondMethod).toBe(firstMethod);
    expect.soft(firstResult.gte(0)).toBe(true);
    // decimal.js nie przechowuje skali: "skala dwa" = co najwyżej dwa miejsca po przecinku.
    expect.soft(firstResult.decimalPlaces()).toBeLessThanOrEqual(2);
    expect.soft(firstResult).toEqual(secondResult);
  });
});
