import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import { SalesCalculations } from '../../src/module1/SalesCalculations.js';

describe('SalesCalculationsTest', () => {
  it('duplicatedCalculationsCurrentlyProduceTheSameResult', () => {
    const unitPrice = new Decimal('100.00');

    const invoice = SalesCalculations.invoiceLineTotal(unitPrice, 1, true);
    const quote = SalesCalculations.quoteLineTotal(unitPrice, 1, true);

    expect(invoice.equals(new Decimal('90.0000'))).toBe(true);
    expect(invoice.toFixed(4)).toBe('90.0000');
    expect(invoice.equals(quote)).toBe(true);
  });
});
