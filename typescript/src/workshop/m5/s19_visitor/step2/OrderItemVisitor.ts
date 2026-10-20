import type { SnackItem } from './SnackItem.js';
import type { TicketItem } from './TicketItem.js';
import type { VoucherItem } from './VoucherItem.js';

/** Krok 2: klasyczny Visitor - jedna metoda na rodzaj pozycji; brak metody = błąd kompilacji. */
export interface OrderItemVisitor<R> {
  visitTicket(ticket: TicketItem): R;

  visitSnack(snack: SnackItem): R;

  visitVoucher(voucher: VoucherItem): R;
}
