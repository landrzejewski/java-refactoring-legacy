import { describe, expect, it } from 'vitest';

import { assertNever } from '../../../../src/shared/assertNever.js';
import { Money } from '../../../../src/workshop/shared/Money.js';
import type { OrderItem } from '../../../../src/workshop/m6/s19_visitor/step2/OrderItem.js';
import type { OrderItemVisitor } from '../../../../src/workshop/m6/s19_visitor/step2/OrderItemVisitor.js';
import { SnackItem } from '../../../../src/workshop/m6/s19_visitor/step2/SnackItem.js';
import { TicketItem } from '../../../../src/workshop/m6/s19_visitor/step2/TicketItem.js';
import { VoucherItem } from '../../../../src/workshop/m6/s19_visitor/step2/VoucherItem.js';
import type * as step3Types from '../../../../src/workshop/m6/s19_visitor/step3/OrderItem.js';
import * as step3Ticket from '../../../../src/workshop/m6/s19_visitor/step3/TicketItem.js';
import * as step3Voucher from '../../../../src/workshop/m6/s19_visitor/step3/VoucherItem.js';

/** Macierz zmian: nowa operacja jest tania zarówno jako Visitor, jak i jako switch po zamkniętej unii. */
describe('S19SolutionTest', () => {
  it('newOperationIsANewVisitor', () => {
    const vatGroup: OrderItemVisitor<string> = {
      visitTicket: () => 'B',
      visitSnack: () => 'A',
      visitVoucher: () => '-',
    };
    const items: OrderItem[] = [new TicketItem('Diuna', 'IMAX', Money.of('40.00')),
      new SnackItem('Cola', Money.of('9.00')), new VoucherItem('KINO20', Money.of('20.00'))];
    expect(items.map((item) => item.accept(vatGroup))).toEqual(['B', 'A', '-']);
  });

  // Odpowiednik newOperationIsANewSwitchInJava25: wyczerpujący switch po kind.
  it('newOperationIsANewSwitchInJava25', () => {
    const items: step3Types.OrderItem[] = [
      new step3Ticket.TicketItem('Diuna', 'IMAX', Money.of('40.00')),
      new step3Voucher.VoucherItem('KINO20', Money.of('20.00'))];
    const vatGroup = (item: step3Types.OrderItem): string => {
      switch (item.kind) {
        case 'ticket': return 'B';
        case 'snack': return 'A';
        case 'voucher': return '-';
        default: return assertNever(item);
      }
    };
    expect(items.map(vatGroup)).toEqual(['B', '-']);
  });
});
