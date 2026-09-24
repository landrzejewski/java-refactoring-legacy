import type { Money } from '../../../shared/Money.js';
import type { LocalDateTime } from '../../../shared/time.js';
import type { TicketData } from '../TicketData.js';
import { RefundService } from './RefundService.js';

/** Start: klient - kasa zwrotów. Sam rozróżnia jeden bilet i wiele biletów. */
export class CancellationDesk {
  private readonly service = new RefundService();

  refund(tickets: readonly TicketData[], now: LocalDateTime): Money {
    const [first] = tickets;
    if (tickets.length === 1 && first !== undefined) {
      return this.service.refund(first, now);
    }
    return this.service.refundAll(tickets, now);
  }
}
