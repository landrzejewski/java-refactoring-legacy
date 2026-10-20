import type { Money } from '../../../shared/Money.js';
import type { OrderItem } from './OrderItem.js';
import type { OrderItemVisitor } from './OrderItemVisitor.js';

/** Krok 1: produkt baru (VAT 23%) z accept. */
export class SnackItem implements OrderItem {
  constructor(readonly name: string, readonly price: Money) {}

  accept<R>(visitor: OrderItemVisitor<R>): R {
    return visitor.visitSnack(this);
  }
}
