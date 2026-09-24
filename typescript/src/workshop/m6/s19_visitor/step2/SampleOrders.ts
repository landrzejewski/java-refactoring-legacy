import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { OrderItem } from './OrderItem.js';
import { SnackItem } from './SnackItem.js';
import { TicketItem } from './TicketItem.js';
import { VoucherItem } from './VoucherItem.js';

/** Krok 2: bez zmian - przykładowe zamówienia (bilety, bar, vouchery). */
export class SampleOrders {
  find(code: string): readonly OrderItem[] {
    switch (code) {
      case 'evening': return Object.freeze([
        new TicketItem('Diuna', 'IMAX', Money.of('40.00')),
        new SnackItem('Popcorn L', Money.of('18.00')),
        new SnackItem('Cola', Money.of('9.00')),
        new VoucherItem('KINO20', Money.of('20.00'))]);
      case 'voucher': return Object.freeze([
        new TicketItem('Amator', '2D', Money.of('25.00')),
        new VoucherItem('KINO20', Money.of('20.00'))]);
      case 'empty': return Object.freeze([]);
      default: throw new IllegalArgumentError(`unknown order: ${code}`);
    }
  }
}
