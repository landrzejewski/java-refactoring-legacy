import type { Money } from '../../../shared/Money.js';
import type { OrderItem } from './OrderItem.js';
import type { OrderItemVisitor } from './OrderItemVisitor.js';

/** Krok 2: bilet (VAT 8%) z accept. */
export class TicketItem implements OrderItem {
  constructor(readonly title: string, readonly format: string, readonly price: Money) {}

  accept<R>(visitor: OrderItemVisitor<R>): R {
    return visitor.visitTicket(this);
  }
}
