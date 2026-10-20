import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import { InMemoryQuoteRepository } from '../../../src/module2/adapter/InMemoryQuoteRepository.js';
import { DeliveryQuote } from '../../../src/module2/domain/DeliveryQuote.js';
import { Parcel } from '../../../src/module2/domain/Parcel.js';
import { ShippingMethod } from '../../../src/module2/domain/ShippingMethod.js';

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
