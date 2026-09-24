import type { Money } from '../../../shared/Money.js';
import type { OrderItem } from './OrderItem.js';

/** Start: produkt baru (VAT 23%). */
export class SnackItem implements OrderItem {
  constructor(readonly name: string, readonly price: Money) {}
}
