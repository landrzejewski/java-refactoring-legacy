import { Decimal } from 'decimal.js';

// Zduplikowana logika (smell): dwie metody liczą to samo niezależnie.
export class SalesCalculations {
  private constructor() {}

  static invoiceLineTotal(unitPrice: Decimal, quantity: number, vip: boolean): Decimal {
    let total = unitPrice.times(quantity);

    if (vip) {
      total = total.times(new Decimal('0.90'));
    }

    return total;
  }

  static quoteLineTotal(unitPrice: Decimal, quantity: number, vip: boolean): Decimal {
    let total = unitPrice.times(quantity);

    if (vip) {
      total = total.times(new Decimal('0.90'));
    }

    return total;
  }
}
