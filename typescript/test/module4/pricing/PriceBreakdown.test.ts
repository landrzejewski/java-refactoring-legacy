import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import {
  ArithmeticError,
  IllegalArgumentError,
  NullPointerError,
} from '../../../src/shared/errors.js';
import { PriceBreakdown } from '../../../src/module4/pricing/PriceBreakdown.js';

const ZERO = new Decimal('0.00');

function breakdownWithBase(base: Decimal | null): PriceBreakdown {
  return new PriceBreakdown(base as Decimal, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO);
}

describe('PriceBreakdownTest', () => {
  it('rejectsAmountsOutsideItsMoneyContract', () => {
    expect.soft(() => breakdownWithBase(null)).toThrow(NullPointerError);
    expect.soft(() => breakdownWithBase(new Decimal('-0.01'))).toThrow(IllegalArgumentError);
    expect.soft(() => breakdownWithBase(new Decimal('1.001'))).toThrow(ArithmeticError);
  });
});
