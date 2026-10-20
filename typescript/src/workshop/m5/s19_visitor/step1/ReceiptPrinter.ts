import { Decimal } from 'decimal.js';

import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { OrderItem } from './OrderItem.js';
import { ReceiptLineVisitor } from './ReceiptLineVisitor.js';
import { SnackItem } from './SnackItem.js';
import { TicketItem } from './TicketItem.js';
import { VoucherItem } from './VoucherItem.js';

/**
 * Krok 1: accept + interfejs Visitora, pierwsza operacja (linia paragonu) przeniesiona
 * do ReceiptLineVisitor. Kwota i VAT jeszcze na instanceof.
 */
export class ReceiptPrinter {
  private readonly lines = new ReceiptLineVisitor();

  print(items: readonly OrderItem[]): string {
    let text = '';
    let total = Money.ZERO;
    let vat = Money.ZERO;
    for (const item of items) {
      text += `${item.accept(this.lines)}\n`;
      total = total.plus(this.amount(item));
      vat = vat.plus(this.vat(item));
    }
    return `${text}Razem: ${total.max(Money.ZERO).toString()}\nVAT: ${vat.toString()}\n`;
  }

  private amount(item: OrderItem): Money {
    if (item instanceof TicketItem) {
      return item.price;
    } else if (item instanceof SnackItem) {
      return item.price;
    } else if (item instanceof VoucherItem) {
      return Money.ZERO.minus(item.value);
    }
    throw new IllegalArgumentError(`unknown item: ${String(item)}`);
  }

  private vat(item: OrderItem): Money {
    if (item instanceof TicketItem) {
      return ReceiptPrinter.vatOf(item.price, 8);
    } else if (item instanceof SnackItem) {
      return ReceiptPrinter.vatOf(item.price, 23);
    } else if (item instanceof VoucherItem) {
      return Money.ZERO;
    }
    throw new IllegalArgumentError(`unknown item: ${String(item)}`);
  }

  /** VAT zawarty w cenie brutto: brutto * stawka / (100 + stawka). */
  private static vatOf(gross: Money, rate: number): Money {
    return new Money(gross.amount.times(rate).dividedBy(100 + rate).toDecimalPlaces(2, Decimal.ROUND_HALF_UP));
  }
}
