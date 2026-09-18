import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import { InMemoryQuoteRepository } from '../../../src/module3/adapter/InMemoryQuoteRepository.js';
import { DeliveryQuote } from '../../../src/module3/domain/DeliveryQuote.js';
import { Parcel } from '../../../src/module3/domain/Parcel.js';
import { ShippingMethod } from '../../../src/module3/domain/ShippingMethod.js';

describe('InMemoryQuoteRepositoryTest', () => {
  it('storesQuote', () => {
    const repository = new InMemoryQuoteRepository();
    const quote = new DeliveryQuote(
      'developer@example.com',
      ShippingMethod.STANDARD,
      new Parcel(new Decimal('3.00')),
      new Decimal('17.28'));

    repository.save(quote);

    expect(repository.quotes()).toEqual([quote]);
  });
});
