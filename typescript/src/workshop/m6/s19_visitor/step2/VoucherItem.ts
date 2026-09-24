import type { Money } from '../../../shared/Money.js';
import type { OrderItem } from './OrderItem.js';
import type { OrderItemVisitor } from './OrderItemVisitor.js';

/** Krok 2: voucher z accept. */
export class VoucherItem implements OrderItem {
  constructor(readonly code: string, readonly value: Money) {}

  accept<R>(visitor: OrderItemVisitor<R>): R {
    return visitor.visitVoucher(this);
  }
}
