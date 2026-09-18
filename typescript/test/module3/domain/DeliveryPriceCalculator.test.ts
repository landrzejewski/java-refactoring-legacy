import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import { IllegalArgumentError } from '../../../src/shared/errors.js';
import { DeliveryPriceCalculator } from '../../../src/module3/domain/DeliveryPriceCalculator.js';
import { ExpressDeliveryPricePolicy } from '../../../src/module3/domain/ExpressDeliveryPricePolicy.js';
import { FuelSurcharge } from '../../../src/module3/domain/FuelSurcharge.js';
import { Parcel } from '../../../src/module3/domain/Parcel.js';
import { ShippingMethod } from '../../../src/module3/domain/ShippingMethod.js';
import { StandardDeliveryPricePolicy } from '../../../src/module3/domain/StandardDeliveryPricePolicy.js';

const deliveryPrices: [ShippingMethod, Decimal][] = [
  [ShippingMethod.STANDARD, new Decimal('17.28')],
  [ShippingMethod.EXPRESS, new Decimal('31.32')],
];

function fuelSurcharge(): FuelSurcharge {
  return new FuelSurcharge(new Decimal('0.08'));
}

function calculatorWithAllPolicies(): DeliveryPriceCalculator {
  const surcharge = fuelSurcharge();
  return new DeliveryPriceCalculator([
    new StandardDeliveryPricePolicy(surcharge),
    new ExpressDeliveryPricePolicy(surcharge),
  ]);
}

describe('DeliveryPriceCalculatorTest', () => {
  it.each(deliveryPrices)('delegatesToPolicySelectedByShippingMethod(%s)', (method, expectedPrice) => {
    const calculator = calculatorWithAllPolicies();

    const price = calculator.priceFor(method, new Parcel(new Decimal('3.00')));

    expect(price).toEqual(expectedPrice);
  });

  it('rejectsDuplicatePolicyForOneShippingMethod', () => {
    const surcharge = fuelSurcharge();

    expect(() => new DeliveryPriceCalculator([
      new StandardDeliveryPricePolicy(surcharge),
      new StandardDeliveryPricePolicy(surcharge),
    ])).toThrow(IllegalArgumentError);
  });

  it('reportsMissingPolicy', () => {
    const calculator = new DeliveryPriceCalculator([new StandardDeliveryPricePolicy(fuelSurcharge())]);

    expect(() => calculator.priceFor(ShippingMethod.EXPRESS, new Parcel(new Decimal('3.00'))))
      .toThrow(IllegalArgumentError);
  });
});
