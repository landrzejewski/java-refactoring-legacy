import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import { Parcel } from '../../../src/module3/domain/Parcel.js';
import { ShippingMethod } from '../../../src/module3/domain/ShippingMethod.js';
import { LegacyDeliveryQuoteService } from '../../../src/module3/legacy/LegacyDeliveryQuoteService.js';

describe('LegacyDeliveryQuoteServiceCharacterizationTest', () => {
  it('documentsStandardDeliveryPriceAndStorage', () => {
    const service = new LegacyDeliveryQuoteService();
    const parcel = new Parcel(new Decimal('3.00'));

    const quote = service.createQuote('developer@example.com', ShippingMethod.STANDARD, parcel);

    expect(quote.price).toEqual(new Decimal('17.28'));
    expect(service.storedQuotes()).toEqual([quote]);
  });

  it('documentsExpressDeliveryPrice', () => {
    const service = new LegacyDeliveryQuoteService();

    const quote = service.createQuote(
      'developer@example.com',
      ShippingMethod.EXPRESS,
      new Parcel(new Decimal('3.00')));

    expect(quote.price).toEqual(new Decimal('31.32'));
  });
});
