import { describe, expect, it } from 'vitest';

import { Money } from '../../../../src/workshop/shared/Money.js';
import { LocalDateTime } from '../../../../src/workshop/shared/time.js';
import * as step2 from '../../../../src/workshop/m6/s12_onemany/step2/RefundService.js';
import { RefundService } from '../../../../src/workshop/m6/s12_onemany/step3/RefundService.js';
import { SingleTicket } from '../../../../src/workshop/m6/s12_onemany/step3/SingleTicket.js';
import { TicketGroup } from '../../../../src/workshop/m6/s12_onemany/step3/TicketGroup.js';
import { TicketData } from '../../../../src/workshop/m6/s12_onemany/TicketData.js';

const NOW = LocalDateTime.of(2026, 10, 2, 12, 0);

/** Composite pozwala zagnieżdżać grupy - potrącenie wciąż jest naliczane raz na zwrot. */
describe('S12SolutionTest', () => {
  it('nestedGroupsAreRefundedAsOneRequest', () => {
    const imax = new SingleTicket(new TicketData(Money.of('40.00'), NOW.plusDays(3)));
    const family = new TicketGroup([
      new SingleTicket(new TicketData(Money.of('25.00'), NOW.plusDays(3))),
      new SingleTicket(new TicketData(Money.of('25.00'), NOW.plusDays(3))),
    ]);
    expect(new RefundService().refund(new TicketGroup([imax, family]), NOW)).toEqual(Money.of('87.00'));
  });

  it('deprecatedWrappersInStep2DelegateToTheNewContract', () => {
    const service = new step2.RefundService();
    const ticket = new TicketData(Money.of('40.00'), NOW.plusDays(3));
    expect(service.refundTicket(ticket, NOW)).toEqual(Money.of('37.00'));
    expect(service.refundAll([ticket, ticket], NOW)).toEqual(Money.of('77.00'));
  });
});
