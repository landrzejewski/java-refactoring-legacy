import type { Money } from '../../../shared/Money.js';
import type { OrderItem } from './OrderItem.js';

/** Start: bilet (VAT 8%). */
export class TicketItem implements OrderItem {
  constructor(readonly title: string, readonly format: string, readonly price: Money) {}
}
