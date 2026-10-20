import { Money } from '../../../shared/Money.js';
import type { OrderItemVisitor } from './OrderItemVisitor.js';
import type { SnackItem } from './SnackItem.js';
import type { TicketItem } from './TicketItem.js';
import type { VoucherItem } from './VoucherItem.js';

/** Krok 2: operacja "kwota do zapłaty" jako Visitor. */
export class AmountVisitor implements OrderItemVisitor<Money> {
  visitTicket(ticket: TicketItem): Money {
    return ticket.price;
  }

  visitSnack(snack: SnackItem): Money {
    return snack.price;
  }

  visitVoucher(voucher: VoucherItem): Money {
    return Money.ZERO.minus(voucher.value);
  }
}
