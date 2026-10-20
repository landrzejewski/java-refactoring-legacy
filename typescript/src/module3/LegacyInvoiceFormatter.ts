import { Decimal } from 'decimal.js';
import type { InvoiceLine } from './InvoiceLine.js';

export class LegacyInvoiceFormatter {
  format(customer: string | null, lines: readonly InvoiceLine[]): string {
    let subtotal = new Decimal(0);
    let result = 'INVOICE\n';

    // String.prototype.toUpperCase() jest niezależne od locale (jak Locale.ROOT).
    const displayedCustomer = customer == null
      ? 'UNKNOWN'
      : customer.trim().toUpperCase();
    result += 'Customer: ' + displayedCustomer + '\n';

    for (const line of lines) {
      const lineTotal = line.unitPrice.times(line.quantity);
      subtotal = subtotal.plus(lineTotal);

      result += line.sku
        + ' x '
        + line.quantity
        + ' = '
        + lineTotal.toDecimalPlaces(2, Decimal.ROUND_HALF_UP).toFixed(2)
        + '\n';
    }

    const tax = subtotal
      .times(new Decimal('0.23'))
      .toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
    const total = subtotal
      .plus(tax)
      .toDecimalPlaces(2, Decimal.ROUND_HALF_UP);

    result += 'Subtotal: '
      + subtotal.toDecimalPlaces(2, Decimal.ROUND_HALF_UP).toFixed(2)
      + '\n';
    result += 'Tax: ' + tax.toFixed(2) + '\n';
    result += 'Total: ' + total.toFixed(2) + '\n';

    return result;
  }
}
