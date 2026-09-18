import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import { Item, OrderSummary, RiskClassifier } from '../../src/module1/RiskClassifier.js';

describe('RiskClassifierTest', () => {
  it('countsIndependentRiskConditions', () => {
    const order = new OrderSummary(
      new Decimal('1500.00'),
      true,
      [new Item(true), new Item(false)],
    );

    expect(RiskClassifier.riskLevel(order)).toBe(3);
  });
});
