import { Decimal } from 'decimal.js';
import { describe, expect, it } from 'vitest';
import { InvoiceFormatter } from '../../src/module2/InvoiceFormatter.js';
import { InvoiceLine } from '../../src/module2/InvoiceLine.js';
import { LegacyInvoiceFormatter } from '../../src/module2/LegacyInvoiceFormatter.js';

const representativeInvoices: [string | null, InvoiceLine[]][] = [
  ['Acme', []],
  [null, [new InvoiceLine('BOOK', 1, new Decimal('10.00'))]],
  ['vip', [
    new InvoiceLine('A', 3, new Decimal('0.10')),
    new InvoiceLine('B', 2, new Decimal('19.995')),
  ]],
];

describe('FormatterEquivalenceTest', () => {
  const legacy = new LegacyInvoiceFormatter();
  const refactored = new InvoiceFormatter();

  it.each(representativeInvoices)('refactoringPreservesObservedOutput(%s)', (customer, lines) => {
    expect(refactored.format(customer, lines)).toBe(legacy.format(customer, lines));
  });
});
