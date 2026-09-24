import type { OrderItemVisitor } from './OrderItemVisitor.js';
import type { SnackItem } from './SnackItem.js';
import type { TicketItem } from './TicketItem.js';
import type { VoucherItem } from './VoucherItem.js';

/** Krok 2: operacja "linia paragonu" jako Visitor. */
export class ReceiptLineVisitor implements OrderItemVisitor<string> {
  visitTicket(ticket: TicketItem): string {
    return `Bilet ${ticket.title} ${ticket.format} ${ticket.price.toString()}`;
  }

  visitSnack(snack: SnackItem): string {
    return `${snack.name} ${snack.price.toString()}`;
  }

  visitVoucher(voucher: VoucherItem): string {
    return `Voucher ${voucher.code} -${voucher.value.toString()}`;
  }
}
