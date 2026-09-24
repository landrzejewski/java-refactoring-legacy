import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';
import type { OrderItemVisitor } from './OrderItemVisitor.js';
import type { SnackItem } from './SnackItem.js';
import type { TicketItem } from './TicketItem.js';
import type { VoucherItem } from './VoucherItem.js';

/** Krok 2: operacja "VAT zawarty w cenie" jako Visitor (bilety 8%, bar 23%, voucher 0). */
export class VatVisitor implements OrderItemVisitor<Money> {
  visitTicket(ticket: TicketItem): Money {
    return VatVisitor.vatOf(ticket.price, 8);
  }

  visitSnack(snack: SnackItem): Money {
    return VatVisitor.vatOf(snack.price, 23);
  }

  visitVoucher(_voucher: VoucherItem): Money {
    return Money.ZERO;
  }

  private static vatOf(gross: Money, rate: number): Money {
    return new Money(gross.amount.times(rate).dividedBy(100 + rate).toDecimalPlaces(2, Decimal.ROUND_HALF_UP));
  }
}
