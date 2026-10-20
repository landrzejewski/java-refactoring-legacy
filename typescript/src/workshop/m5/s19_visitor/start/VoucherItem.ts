import type { Money } from '../../../shared/Money.js';
import type { OrderItem } from './OrderItem.js';

/** Start: voucher - pomniejsza kwotę do zapłaty, bez VAT. */
export class VoucherItem implements OrderItem {
  constructor(readonly code: string, readonly value: Money) {}
}
