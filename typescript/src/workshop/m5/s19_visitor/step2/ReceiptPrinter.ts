import { Money } from '../../../shared/Money.js';
import { AmountVisitor } from './AmountVisitor.js';
import type { OrderItem } from './OrderItem.js';
import { ReceiptLineVisitor } from './ReceiptLineVisitor.js';
import { VatVisitor } from './VatVisitor.js';

/**
 * Krok 2: wszystkie operacje jako Visitory - żadnego instanceof. Nowa operacja = nowa klasa
 * Visitora; nowy rodzaj pozycji = zmiana interfejsu i WSZYSTKICH Visitorów.
 */
export class ReceiptPrinter {
  private readonly lines = new ReceiptLineVisitor();
  private readonly amounts = new AmountVisitor();
  private readonly vats = new VatVisitor();

  print(items: readonly OrderItem[]): string {
    let text = '';
    let total = Money.ZERO;
    let vat = Money.ZERO;
    for (const item of items) {
      text += `${item.accept(this.lines)}\n`;
      total = total.plus(item.accept(this.amounts));
      vat = vat.plus(item.accept(this.vats));
    }
    return `${text}Razem: ${total.max(Money.ZERO).toString()}\nVAT: ${vat.toString()}\n`;
  }
}
