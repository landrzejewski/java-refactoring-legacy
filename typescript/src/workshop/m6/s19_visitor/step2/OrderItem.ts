import type { OrderItemVisitor } from './OrderItemVisitor.js';

/** Krok 2: element przyjmuje odwiedzającego (double dispatch). */
export interface OrderItem {
  accept<R>(visitor: OrderItemVisitor<R>): R;
}
