import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import { IllegalStateError } from '../../../src/shared/errors.js';
import { Command, CreateDeliveryQuote } from '../../../src/module3/application/CreateDeliveryQuote.js';
import type { QuoteRepository } from '../../../src/module3/application/QuoteRepository.js';
import { DeliveryPriceCalculator } from '../../../src/module3/domain/DeliveryPriceCalculator.js';
import type { DeliveryQuote } from '../../../src/module3/domain/DeliveryQuote.js';
import { FuelSurcharge } from '../../../src/module3/domain/FuelSurcharge.js';
import { Parcel } from '../../../src/module3/domain/Parcel.js';
import { ShippingMethod } from '../../../src/module3/domain/ShippingMethod.js';
import { StandardDeliveryPricePolicy } from '../../../src/module3/domain/StandardDeliveryPricePolicy.js';

function calculator(): DeliveryPriceCalculator {
  const surcharge = new FuelSurcharge(new Decimal('0.08'));
  return new DeliveryPriceCalculator([new StandardDeliveryPricePolicy(surcharge)]);
}

describe('CreateDeliveryQuoteTest', () => {
  it('calculatesStoresAndNotifiesAboutQuote', () => {
    const savedQuotes: DeliveryQuote[] = [];
    const notifications: DeliveryQuote[] = [];
    const useCase = new CreateDeliveryQuote(
      calculator(),
      { save: (quote) => savedQuotes.push(quote) },
      { quoteCreated: (quote) => notifications.push(quote) });

    const result = useCase.execute(new Command(
      'developer@example.com',
      ShippingMethod.STANDARD,
      new Parcel(new Decimal('3.00'))));

    expect(result.price).toEqual(new Decimal('17.28'));
    expect(savedQuotes).toEqual([result]);
    expect(notifications).toEqual([result]);
  });

  it('doesNotNotifyWhenSavingFails', () => {
    const notifications: DeliveryQuote[] = [];
    const failingRepository: QuoteRepository = {
      save: () => {
        throw new IllegalStateError('Storage unavailable');
      },
    };
    const useCase = new CreateDeliveryQuote(
      calculator(),
      failingRepository,
      { quoteCreated: (quote) => notifications.push(quote) });

    expect(() => useCase.execute(new Command(
      'developer@example.com',
      ShippingMethod.STANDARD,
      new Parcel(new Decimal('3.00'))))).toThrow(IllegalStateError);
    expect(notifications).toHaveLength(0);
  });
});
