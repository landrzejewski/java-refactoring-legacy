import type { Decimal } from 'decimal.js';

export class InvoiceLine {
  constructor(
    readonly sku: string,
    readonly quantity: number,
    readonly unitPrice: Decimal,
  ) {}
}
