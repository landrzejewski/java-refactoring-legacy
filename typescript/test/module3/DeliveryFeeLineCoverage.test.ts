import { describe, expect, it } from 'vitest';
import { DeliveryFee } from '../../src/module3/DeliveryFee.js';

describe('DeliveryFeeLineCoverageTest', () => {
  it('premiumCustomerHasFreeDelivery', () => {
    expect(DeliveryFee.fee(true)).toBe(0);
  });
});
