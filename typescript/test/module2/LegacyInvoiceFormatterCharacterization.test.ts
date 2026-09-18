import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import { InvoiceLine } from '../../src/module2/InvoiceLine.js';
import { LegacyInvoiceFormatter } from '../../src/module2/LegacyInvoiceFormatter.js';

describe('LegacyInvoiceFormatterCharacterizationTest', () => {
  const formatter = new LegacyInvoiceFormatter();

  it('documentsCurrentFormattingAndRounding', () => {
    const lines = [
      new InvoiceLine('BOOK', 2, new Decimal('19.99')),
      new InvoiceLine('PEN', 1, new Decimal('5.00')),
    ];

    const result = formatter.format('  Acme  ', lines);

    expect(result).toBe([
      'INVOICE',
      'Customer: ACME',
      'BOOK x 2 = 39.98',
      'PEN x 1 = 5.00',
      'Subtotal: 44.98',
      'Tax: 10.35',
      'Total: 55.33',
      '',
    ].join('\n'));
  });

  it('documentsCurrentFallbackForMissingCustomer', () => {
    const result = formatter.format(null, []);

    expect(result).toBe([
      'INVOICE',
      'Customer: UNKNOWN',
      'Subtotal: 0.00',
      'Tax: 0.00',
      'Total: 0.00',
      '',
    ].join('\n'));
  });
});
