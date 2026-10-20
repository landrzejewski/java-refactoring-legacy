import { Decimal } from 'decimal.js';

import { assertNever } from '../../../../shared/assertNever.js';
import { Money } from '../../../shared/Money.js';
import type { OrderItem } from './OrderItem.js';

/**
 * Krok 3: operacje jako wyczerpujące switche po zamkniętej unii. default wywołuje assertNever,
 * więc nie wyłącza kontroli kompilatora - nowy rodzaj pozycji to błąd kompilacji w każdym switchu.
 * Każda operacja w jednym miejscu (jak Visitor), bez ceremonii accept/visit i bez double dispatch.
 */
export class ReceiptPrinter {
  print(items: readonly OrderItem[]): string {
    let text = '';
    let total = Money.ZERO;
    let vat = Money.ZERO;
    for (const item of items) {
      text += `${ReceiptPrinter.line(item)}\n`;
      total = total.plus(ReceiptPrinter.amount(item));
      vat = vat.plus(ReceiptPrinter.vat(item));
    }
    return `${text}Razem: ${total.max(Money.ZERO).toString()}\nVAT: ${vat.toString()}\n`;
  }

  static line(item: OrderItem): string {
    switch (item.kind) {
      case 'ticket': {
        const { title, format, price } = item;
        return `Bilet ${title} ${format} ${price.toString()}`;
      }
      case 'snack': {
        const { name, price } = item;
        return `${name} ${price.toString()}`;
      }
      case 'voucher': {
        const { code, value } = item;
        return `Voucher ${code} -${value.toString()}`;
      }
      default: return assertNever(item);
    }
  }

  static amount(item: OrderItem): Money {
    switch (item.kind) {
      case 'ticket': return item.price;
      case 'snack': return item.price;
      case 'voucher': return Money.ZERO.minus(item.value);
      default: return assertNever(item);
    }
  }

  static vat(item: OrderItem): Money {
    switch (item.kind) {
      case 'ticket': return ReceiptPrinter.vatOf(item.price, 8);
      case 'snack': return ReceiptPrinter.vatOf(item.price, 23);
      case 'voucher': return Money.ZERO;
      default: return assertNever(item);
    }
  }

  private static vatOf(gross: Money, rate: number): Money {
    return new Money(gross.amount.times(rate).dividedBy(100 + rate).toDecimalPlaces(2, Decimal.ROUND_HALF_UP));
  }
}
