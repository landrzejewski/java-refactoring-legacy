import { Decimal } from 'decimal.js';
import type { InvoiceLine } from './InvoiceLine.js';

const TAX_RATE = new Decimal('0.23');

export class InvoiceFormatter {
  format(customer: string | null, lines: readonly InvoiceLine[]): string {
    const subtotal = InvoiceFormatter.calculateSubtotal(lines);
    const tax = InvoiceFormatter.money(subtotal.times(TAX_RATE));
    const total = InvoiceFormatter.money(subtotal.plus(tax));

    const result: string[] = [
      'INVOICE\n',
      'Customer: ', InvoiceFormatter.displayedCustomer(customer), '\n',
    ];

    InvoiceFormatter.appendLines(result, lines);

    result.push(
      'Subtotal: ', InvoiceFormatter.text(InvoiceFormatter.money(subtotal)), '\n',
      'Tax: ', InvoiceFormatter.text(tax), '\n',
      'Total: ', InvoiceFormatter.text(total), '\n',
    );
    return result.join('');
  }

  private static displayedCustomer(customer: string | null): string {
    return customer == null
      ? 'UNKNOWN'
      : customer.trim().toUpperCase();
  }

  private static calculateSubtotal(lines: readonly InvoiceLine[]): Decimal {
    return lines
      .map(InvoiceFormatter.lineTotal)
      .reduce((sum, value) => sum.plus(value), new Decimal(0));
  }

  private static appendLines(result: string[], lines: readonly InvoiceLine[]): void {
    for (const line of lines) {
      result.push(
        line.sku,
        ' x ',
        String(line.quantity),
        ' = ',
        InvoiceFormatter.text(InvoiceFormatter.money(InvoiceFormatter.lineTotal(line))),
        '\n',
      );
    }
  }

  private static lineTotal(line: InvoiceLine): Decimal {
    return line.unitPrice.times(line.quantity);
  }

  private static money(value: Decimal): Decimal {
    return value.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }

  // Kwota o skali 2 w postaci tekstowej (BigDecimal drukuje swoją skalę).
  private static text(money: Decimal): string {
    return money.toFixed(2);
  }
}
