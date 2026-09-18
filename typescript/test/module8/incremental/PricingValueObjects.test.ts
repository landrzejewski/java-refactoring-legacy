import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import { IllegalArgumentError, NullPointerError } from '../../../src/shared/errors.js';
import { PriceQuote } from '../../../src/module8/incremental/PriceQuote.js';
import { PriceRequest } from '../../../src/module8/incremental/PriceRequest.js';

const NULL_DECIMAL = null as unknown as Decimal;

function assertFailure(
  expectedType: new (...args: never[]) => Error,
  expectedMessage: string,
  action: () => unknown,
): void {
  let failure: unknown;
  try {
    action();
  } catch (error) {
    failure = error;
  }
  expect(failure).toBeInstanceOf(expectedType);
  expect((failure as Error).message).toBe(expectedMessage);
}

describe('PricingValueObjectsTest', () => {
  it('requestNormalizesMoneyAndRateUsingTheDeclaredPolicy', () => {
    const request = new PriceRequest(new Decimal('12.345'), 2, new Decimal('0.12345'));

    expect(request.unitPrice.equals(new Decimal('12.34'))).toBe(true);
    expect(request.discountRate.equals(new Decimal('0.1234'))).toBe(true);
    expect(request.unitPrice.decimalPlaces()).toBe(2);
    expect(request.discountRate.decimalPlaces()).toBe(4);
  });

  it('requestRejectsInvalidValuesBeforeRounding', () => {
    assertFailure(NullPointerError, 'unitPrice', () => new PriceRequest(NULL_DECIMAL, 1, new Decimal(0)));
    assertFailure(NullPointerError, 'discountRate', () => new PriceRequest(new Decimal(1), 1, NULL_DECIMAL));
    assertFailure(
      IllegalArgumentError,
      'unitPrice must not be negative',
      () => new PriceRequest(new Decimal('-0.001'), 1, new Decimal(0)),
    );
    assertFailure(
      IllegalArgumentError,
      'quantity must be positive',
      () => new PriceRequest(new Decimal(1), 0, new Decimal(0)),
    );
    assertFailure(
      IllegalArgumentError,
      'discountRate must be between 0 and 1',
      () => new PriceRequest(new Decimal(1), 1, new Decimal('-0.00001')),
    );
    assertFailure(
      IllegalArgumentError,
      'discountRate must be between 0 and 1',
      () => new PriceRequest(new Decimal(1), 1, new Decimal('1.00001')),
    );
  });

  it('quoteNormalizesMoneyAndRejectsInvalidAmounts', () => {
    expect(new PriceQuote(new Decimal('10.125')).netAmount.equals(new Decimal('10.12'))).toBe(true);
    assertFailure(NullPointerError, 'netAmount', () => new PriceQuote(NULL_DECIMAL));
    assertFailure(
      IllegalArgumentError,
      'netAmount must not be negative',
      () => new PriceQuote(new Decimal('-0.001')),
    );
  });
});
