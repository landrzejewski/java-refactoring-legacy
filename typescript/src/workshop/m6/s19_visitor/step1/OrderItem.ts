import type { OrderItemVisitor } from './OrderItemVisitor.js';

/** Krok 1: element przyjmuje odwiedzającego (double dispatch). */
export interface OrderItem {
  accept<R>(visitor: OrderItemVisitor<R>): R;
}
