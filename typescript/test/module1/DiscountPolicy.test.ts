import { describe, expect, it } from 'vitest';
import { DiscountPolicy } from '../../src/module1/DiscountPolicy.js';

describe('DiscountPolicyTest', () => {
  it('combinesThresholdAndVipDiscount', () => {
    expect(DiscountPolicy.discountPercent(100, true)).toBe(15);
  });
});
